package org.liquibase.ext.couchbase.plugin.common;

import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import lombok.SneakyThrows;
import org.liquibase.ext.couchbase.plugin.containers.JavaMavenContainer;
import org.liquibase.ext.couchbase.plugin.util.TestPropertyProvider;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TestContainerInitializer {
    private static final String COUCHBASE_IMAGE_NAME = TestPropertyProvider.getProperty("couchbase.image.name");
    private static final String COUCHBASE_IMAGE_VERSION = TestPropertyProvider.getProperty("couchbase.version");
    private static final String TEST_PROJECT_ABSOLUTE_PATH = System.getProperty("user.dir");
    private static final String TEST_PROJECT_MAIN_RESOURCES_PATH = "/test-project/src/main/resources/";
    public static final String ROOT_WITH_TEST_PROJECT_TEST_RESOURCES = TEST_PROJECT_ABSOLUTE_PATH + "/src/test/resources/";
    private static final String COUCHBASE_NETWORK_ALIAS = "couchbase";
    private static final String LIQUIBASE_COUCHBASE_PROJECT_BASE_DIR = "/liquibase-couchbase-extension";
    private static final String LIQUIBASE_PROPERTIES_FILE = "liquibase.properties";
    private static final String DEPENDENCIES_VOLUME = "/dependencies";
    private static final String MAVEN_REPOSITORY_PATH = "/root/.m2/repository";
    private static final String BUILD_DEPENDENCY_SH = "build-dependency.sh";
    private static final String LIQUIBASE_UPDATE_COMMAND_SH = "liquibase-update-command.sh";
    private static final String LIQUIBASE_COUCHBASE_PROPERTIES = "liquibase-couchbase.properties";

    public static CouchbaseLiquibaseDatabase createDatabase(CouchbaseContainer container) {
        return new CouchbaseLiquibaseDatabase(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        );
    }

    public static CouchbaseContainer createCouchbaseContainer(String testBucket) {
        Network network = Network.newNetwork();
        BucketDefinition bucketDef = new BucketDefinition(testBucket).withPrimaryIndex(false);

        try (CouchbaseContainer couchbaseContainer = new CouchbaseContainer(
                DockerImageName.parse(COUCHBASE_IMAGE_NAME).withTag(COUCHBASE_IMAGE_VERSION))
                .withBucket(bucketDef)
                .withServiceQuota(CouchbaseService.KV, 512)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .withNetwork(network)
                .withNetworkAliases(COUCHBASE_NETWORK_ALIAS)
                .waitingFor(Wait.forHealthcheck())) {
            return couchbaseContainer;
        }
    }

    public static JavaMavenContainer createMavenPluginContainer(CouchbaseContainer couchbaseContainer, String changelog) {
        MountableFile changelogFile = MountableFile.forHostPath(ROOT_WITH_TEST_PROJECT_TEST_RESOURCES + changelog);
        MountableFile liquibaseCouchbaseFile = MountableFile.forHostPath(
                ROOT_WITH_TEST_PROJECT_TEST_RESOURCES + "mvntest/" + LIQUIBASE_COUCHBASE_PROPERTIES);
        MountableFile credentialsFile = MountableFile.forHostPath(ROOT_WITH_TEST_PROJECT_TEST_RESOURCES + LIQUIBASE_PROPERTIES_FILE);
        MountableFile liquibaseUpdateShFile = MountableFile.forHostPath(
                ROOT_WITH_TEST_PROJECT_TEST_RESOURCES + LIQUIBASE_UPDATE_COMMAND_SH);

        try (JavaMavenContainer javaMavenPluginContainer = new JavaMavenContainer()
                .withFileSystemBind(getRootPath() + DEPENDENCIES_VOLUME, MAVEN_REPOSITORY_PATH)
                .withNetwork(couchbaseContainer.getNetwork())
                .withAccessToHost(true)
                .withCopyFileToContainer(MountableFile.forHostPath(TEST_PROJECT_ABSOLUTE_PATH), "/test-project")
                .withCopyFileToContainer(changelogFile, TEST_PROJECT_MAIN_RESOURCES_PATH + "liquibase/changelog-root.xml")
                .withCopyFileToContainer(liquibaseCouchbaseFile, TEST_PROJECT_MAIN_RESOURCES_PATH + LIQUIBASE_COUCHBASE_PROPERTIES)
                .withCopyFileToContainer(credentialsFile, TEST_PROJECT_MAIN_RESOURCES_PATH + "liquibase/liquibase.properties")
                .withCopyFileToContainer(liquibaseUpdateShFile, LIQUIBASE_UPDATE_COMMAND_SH)
                .dependsOn(couchbaseContainer)
                .withCommand("sh liquibase-update-command.sh")) {
            return javaMavenPluginContainer;
        }
    }

    @SneakyThrows
    public static JavaMavenContainer createJavaMavenContainerToBuildDependency() {
        try (JavaMavenContainer javaMavenContainer = new JavaMavenContainer()) {
            javaMavenContainer
                    .withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString(),
                            LIQUIBASE_COUCHBASE_PROJECT_BASE_DIR,
                            BindMode.READ_WRITE)
                    .withFileSystemBind(getRootPath() + DEPENDENCIES_VOLUME,
                            MAVEN_REPOSITORY_PATH)
                    .withCopyFileToContainer(MountableFile.forClasspathResource(BUILD_DEPENDENCY_SH), BUILD_DEPENDENCY_SH)
                    .withCommand("sh " + BUILD_DEPENDENCY_SH)
                    .start();
            while (javaMavenContainer.isRunning()) {
                TimeUnit.SECONDS.sleep(5L);
            }
            return javaMavenContainer;
        }
    }

    public static Path getPathOfLiquibaseCouchbaseParentProject() {
        return Paths.get(TEST_PROJECT_ABSOLUTE_PATH).getParent();
    }

    public static String getRootPath() {
        return getPathOfLiquibaseCouchbaseParentProject().toString();
    }

}

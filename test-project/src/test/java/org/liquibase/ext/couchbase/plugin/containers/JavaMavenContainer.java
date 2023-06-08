package org.liquibase.ext.couchbase.plugin.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.ROOT_WITH_TEST_PROJECT_TEST_RESOURCES;

public class JavaMavenContainer extends GenericContainer<JavaMavenContainer> {

    private static final String DOCKERFILE = "Dockerfile";

    public JavaMavenContainer() {
        super(new ImageFromDockerfile().withFileFromPath(DOCKERFILE,
                Paths.get(ROOT_WITH_TEST_PROJECT_TEST_RESOURCES + DOCKERFILE)));
    }
}

package org.liquibase.ext.couchbase.cli.test.containers;

import org.liquibase.ext.couchbase.cli.test.util.TestPropertyProvider;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class LiquibaseContainer extends GenericContainer<LiquibaseContainer> {
    private static final String LIQUIBASE_IMAGE_NAME = TestPropertyProvider.getProperty("liquibase.image.name");
    private static final String LIQUIBASE_IMAGE_VERSION = TestPropertyProvider.getProperty("liquibase.version");

    public LiquibaseContainer() {
        super(DockerImageName.parse(LIQUIBASE_IMAGE_NAME).withTag(LIQUIBASE_IMAGE_VERSION));
    }
}

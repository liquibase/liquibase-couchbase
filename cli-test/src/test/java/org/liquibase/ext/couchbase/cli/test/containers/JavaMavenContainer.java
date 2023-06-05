package org.liquibase.ext.couchbase.cli.test.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class JavaMavenContainer extends GenericContainer<JavaMavenContainer> {

    public JavaMavenContainer() {
        super(new ImageFromDockerfile().withFileFromClasspath("Dockerfile", "Dockerfile"));
    }
}

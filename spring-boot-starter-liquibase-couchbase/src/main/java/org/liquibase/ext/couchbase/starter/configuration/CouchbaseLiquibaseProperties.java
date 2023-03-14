package org.liquibase.ext.couchbase.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "spring.liquibase.couchbase", ignoreUnknownFields = false)
public class CouchbaseLiquibaseProperties {

    private String changeLog = "classpath:/db/changelog/db.changelog-master.yaml";

}

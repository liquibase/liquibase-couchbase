package org.liquibase.ext.couchbase.starter.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(CouchbaseLiquibase.class)
@EnableConfigurationProperties(CouchbaseLiquibaseProperties.class)
public class CouchbaseLiquibaseAutoConfiguration {

    private final CouchbaseLiquibaseProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public CouchbaseLiquibase couchbaseLiquibase() {
        CouchbaseLiquibase couchbaseLiquibase = new CouchbaseLiquibase();
        couchbaseLiquibase.setChangeLog(properties.getChangeLog());
        return couchbaseLiquibase;
    }
}

package org.liquibase.ext.couchbase.starter.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(CouchbaseLiquibase.class)
public class CouchbaseLiquibaseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CouchbaseLiquibase couchbaseLiquibase() {
        return new CouchbaseLiquibase();
    }
}

package liquibase.ext.couchbase.configuration;

import liquibase.configuration.AutoloadedConfigurations;
import liquibase.configuration.ConfigurationDefinition;

import java.time.Duration;
import java.util.Optional;

/**
 * Configuration class specific for this extension
 */
public class CouchbaseLiquibaseConfiguration implements AutoloadedConfigurations {

    public static final ConfigurationDefinition<String> SERVICE_BUCKET_NAME;
    public static final ConfigurationDefinition<String> CHANGELOG_LOCK_COLLECTION_NAME;

    public static final ConfigurationDefinition<Duration> CHANGELOG_WAIT_TIME;
    public static final ConfigurationDefinition<Duration> CHANGELOG_RECHECK_TIME;
    public static final ConfigurationDefinition<Duration> LOCK_TTL;
    public static final ConfigurationDefinition<Duration> LOCK_TTL_PROLONGATION;
    public static final ConfigurationDefinition<Duration> TRANSACTION_TIMEOUT;
    public static final ConfigurationDefinition<Duration> MUTATE_IN_TIMEOUT;
    public static final ConfigurationDefinition<Boolean> IS_REACTIVE_TRANSACTIONS;
    public static final ConfigurationDefinition<Integer> REACTIVE_TRANSACTION_PARALLEL_THREADS;

    static {
        ConfigurationDefinition.Builder builder = new ConfigurationDefinition.Builder("liquibase.couchbase");

        CHANGELOG_RECHECK_TIME = builder.define("lockservice.changelogRecheckTime", Duration.class)
                .addAliasKey("changelogRecheckTime")
                .setDescription("Change log recheck time")
                .setDefaultValue(Duration.ofSeconds(10L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        CHANGELOG_WAIT_TIME = builder.define("lockservice.changelogWaitTime", Duration.class)
                .addAliasKey("changelogWaitTime")
                .setDescription("Time limit to wait for lock in LockService")
                .setDefaultValue(Duration.ofSeconds(300L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        CHANGELOG_LOCK_COLLECTION_NAME = builder.define("lockservice.changelogCollectionName", String.class)
                .addAliasKey("changelogCollectionName")
                .setDescription("Collection name in service bucket")
                .setDefaultValue("CHANGELOGLOCKS")
                .build();

        SERVICE_BUCKET_NAME = builder.define("serviceBucketName", String.class)
                .addAliasKey("serviceBucketName")
                .setDescription("Liquibase service bucket name")
                .setDefaultValue("liquibaseServiceBucket")
                .build();

        LOCK_TTL_PROLONGATION = builder.define("lockservice.ttlProlongation", Duration.class)
                .addAliasKey("ttlProlongation")
                .setDescription("Liquibase locks prolongation time")
                .setDefaultValue(Duration.ofMinutes(1L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        LOCK_TTL = builder.define("lockservice.lockTtl", Duration.class)
                .addAliasKey("lockTtl")
                .setDescription("Liquibase locks time to live")
                .setDefaultValue(Duration.ofMinutes(3L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        TRANSACTION_TIMEOUT = builder.define("transaction.timeout", Duration.class)
                .addAliasKey("transactionTimeout")
                .setDescription("Transactions timeout")
                .setDefaultValue(Duration.ofSeconds(15))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        MUTATE_IN_TIMEOUT = builder.define("mutateIn.timeout", Duration.class)
                .addAliasKey("mutateInTimeout")
                .setDescription("MutateIn operation timeout")
                .setDefaultValue(Duration.ofSeconds(2))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        IS_REACTIVE_TRANSACTIONS = builder.define("transaction.reactive.enabled", Boolean.class)
                .addAliasKey("transactionReactiveEnabled")
                .setDescription("Flag if transactions is reactive")
                .setDefaultValue(false)
                .build();

        REACTIVE_TRANSACTION_PARALLEL_THREADS = builder.define("transaction.reactive.threads", Integer.class)
                .addAliasKey("transactionReactiveThreads")
                .setDescription("Number of parallel threads for executing statements in reactive transaction")
                .setDefaultValue(16)
                .build();
    }

    public static boolean isReactiveTransactions() {
        return IS_REACTIVE_TRANSACTIONS.getCurrentValue();
    }

    public static Duration getChangelogWaitTime() {
        return CHANGELOG_WAIT_TIME.getCurrentValue();
    }

    public static Duration getChangelogRecheckTime() {
        return CHANGELOG_RECHECK_TIME.getCurrentValue();
    }

    private static Duration durationExtract(Object value) {
        return Optional.ofNullable(value)
                .map(String::valueOf)
                .map(Duration::parse)
                .orElse(null);
    }
}

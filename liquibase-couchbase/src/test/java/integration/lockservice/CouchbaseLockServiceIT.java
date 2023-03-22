package integration.lockservice;

import com.couchbase.client.java.Collection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.CouchbaseContainerizedTest;
import liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration;
import liquibase.ext.couchbase.lockservice.CouchbaseLockService;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import lombok.SneakyThrows;
import static common.matchers.CouchbaseBucketAssert.assertThat;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.SERVICE_BUCKET_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CouchbaseLockServiceIT extends CouchbaseContainerizedTest {

    private static final String LOCK_ID = SERVICE_BUCKET_NAME;

    private static final CouchbaseLockService lockService = new CouchbaseLockService();
    private static final ContextServiceProvider serviceProvider = new ContextServiceProvider(database);
    private static final ClusterOperator clusterOperator = new ClusterOperator(cluster);
    private static final String lockCollectionName = CouchbaseLiquibaseConfiguration.CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue();

    @BeforeAll
    static void setUp() {
        lockService.setDatabase(database);
        lockService.setServiceProvider(serviceProvider);
        lockService.setChangeLogLockRecheckTime(1000);
        lockService.setChangeLogLockWaitTime(1000);
        if (serviceBucketExists()) {
            cluster.buckets().dropBucket(SERVICE_BUCKET_NAME);
        }
    }

    @AfterAll
    @SneakyThrows
    static void tearDown() {
        lockService.reset();
    }

    @Test
    @Order(1)
    @SneakyThrows
    @DisplayName("Assert that lock collection exists after first initialization")
    void lockCollectionExistsAfterInit() {
        lockService.init();

        assertTrue(serviceBucketExists());
        assertThat(cluster.bucket(SERVICE_BUCKET_NAME)).hasCollectionInScope(lockCollectionName, DEFAULT_SERVICE_SCOPE);
    }

    @Test
    @Order(2)
    @SneakyThrows
    @DisplayName("Assert successful lock acquiring on serviceBucket with current service id")
    void lockAcquire() {
        Collection serviceCollection = getServiceCollection();
        String serviceId = lockService.getServiceId();

        boolean lockExistsBeforeAcquiring = serviceCollection.exists(LOCK_ID).exists();
        boolean isAcquired = lockService.acquireLock();

        assertTrue(isAcquired);
        assertFalse(lockExistsBeforeAcquiring);
        assertThat(serviceCollection).hasLockHeldBy("liquibaseServiceBucket", serviceId);
    }


    @Test
    @Order(3)
    @SneakyThrows
    @DisplayName("Lock should not exists database after release")
    void lockDocumentExists() {
        Collection serviceCollection = getServiceCollection();

        lockService.releaseLock();

        assertThat(serviceCollection).doesNotContainId("liquibaseServiceBucket");
    }


    private static boolean serviceBucketExists() {
        return clusterOperator.isBucketExists(SERVICE_BUCKET_NAME);
    }

    private Collection getServiceCollection() {
        return serviceProvider.getServiceCollection(lockCollectionName);
    }

}

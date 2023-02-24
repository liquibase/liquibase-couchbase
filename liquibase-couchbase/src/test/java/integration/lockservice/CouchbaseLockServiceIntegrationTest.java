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
import liquibase.ext.couchbase.lockservice.CouchbaseLockService;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import lombok.SneakyThrows;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.lockservice.CouchbaseLockService.LOCK_COLLECTION_NAME;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.FALLBACK_SERVICE_BUCKET_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CouchbaseLockServiceIntegrationTest extends CouchbaseContainerizedTest {

    private static final String LOCK_ID = FALLBACK_SERVICE_BUCKET_NAME;

    private static final CouchbaseLockService lockService = new CouchbaseLockService();
    private static ContextServiceProvider serviceProvider;
    private final ClusterOperator clusterOperator = new ClusterOperator(cluster);

    @BeforeAll
    static void setUp() {
        lockService.setDatabase(database);
        serviceProvider = new ContextServiceProvider(database);
        lockService.setServiceProvider(serviceProvider);
        lockService.setChangeLogLockRecheckTime(1000);
        lockService.setChangeLogLockWaitTime(1000);
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
        assertThat(cluster.bucket(FALLBACK_SERVICE_BUCKET_NAME)).hasCollectionInScope(LOCK_COLLECTION_NAME, DEFAULT_SERVICE_SCOPE);
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

        assertThat(serviceCollection).hasNoDocument("liquibaseServiceBucket");
    }


    private boolean serviceBucketExists() {
        return clusterOperator.isBucketExists(FALLBACK_SERVICE_BUCKET_NAME);
    }

    private Collection getServiceCollection() {
        return serviceProvider.getServiceCollection(LOCK_COLLECTION_NAME);
    }

}

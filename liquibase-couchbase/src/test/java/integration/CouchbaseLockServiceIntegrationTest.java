package integration;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.ExistsResult;
import common.CouchbaseContainerizedTest;
import liquibase.exception.LockException;
import liquibase.ext.couchbase.lockservice.CouchbaseLockService;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.TimeUnit;

import static common.matchers.CouchBaseBucketAssert.assertThat;
import static liquibase.ext.couchbase.lockservice.CouchbaseLockService.LOCK_COLLECTION_NAME;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.FALLBACK_SERVICE_BUCKET_NAME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
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
    @DisplayName("Assert that lock collection not exists before first initialization and lock acquiring")
    void lockCollectionNotExists() {
        assertFalse(lockService.hasChangeLogLock());
        assertFalse(serviceBucketExists());
    }

    @Test
    @Order(2)
    @SneakyThrows
    @DisplayName("Assert that lock collection exists after first initialization")
    void lockCollectionExistsAfterInit() {

        lockService.init();
        assertTrue(serviceBucketExists());

        assertThat(cluster.bucket(FALLBACK_SERVICE_BUCKET_NAME)).hasCollectionInScope(LOCK_COLLECTION_NAME, DEFAULT_SERVICE_SCOPE);

    }

    @Test
    @Order(3)
    @SneakyThrows
    @DisplayName("Successful lock acquiring")
    void lockAcquire() {

        Collection serviceCollection = getServiceCollection();
        assertFalse(serviceCollection.exists(LOCK_ID).exists());

        lockService.acquireLock();
        assertTrue(serviceCollection.exists("liquibaseServiceBucket").exists());

    }

    @Test
    @Order(4)
    @SneakyThrows
    @DisplayName("Lock document exists after lock acquiring and releasing")
    void lockDocumentExists() {

        lockService.releaseLock();

        Collection serviceCollection = getServiceCollection();
        ExistsResult result = serviceCollection.exists("liquibaseServiceBucket");
        assertFalse(result.exists());

    }

    @Test
    @Order(5)
    @SneakyThrows
    @DisplayName("Throwing exception when lock already acquired")
    void lockAlreadyAcquired() {

        lockService.waitForLock();
        assertTrue(lockService.hasChangeLogLock());

        assertThatExceptionOfType(LockException.class)
            .isThrownBy(lockService::waitForLock)
            .withMessage("Could not acquire lock");

    }

    @Test
    @Order(6)
    @SneakyThrows
    @DisplayName("Releasing lock after lock exception")
    void releaseLock() {

        assertTrue(lockService.hasChangeLogLock());

        lockService.releaseLock();
        assertFalse(lockService.hasChangeLogLock());

    }

    @Test
    @Order(7)
    @SneakyThrows
    @DisplayName("Reacquiring lock after lock exception")
    void acquireThenReleaseLock() {

        assertFalse(lockService.hasChangeLogLock());

        lockService.setChangeLogLockWaitTime(10000);
        lockService.waitForLock();
        releaseLockAfter(1000);

        lockService.waitForLock();
        assertTrue(lockService.hasChangeLogLock());
    }

    private void releaseLockAfter(long millis) {
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(millis);
                lockService.releaseLock();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private boolean serviceBucketExists() {
        return clusterOperator.isBucketExists(FALLBACK_SERVICE_BUCKET_NAME);
    }

    private Collection getServiceCollection() {
        return serviceProvider.getServiceCollection(LOCK_COLLECTION_NAME);
    }

}

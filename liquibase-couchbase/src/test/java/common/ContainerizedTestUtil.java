package common;

import liquibase.ext.couchbase.database.ConnectionData;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import lombok.experimental.UtilityClass;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;

import java.time.Duration;

import static common.constants.TestConstants.CB_IMAGE_NAME;

@UtilityClass
public class ContainerizedTestUtil {

    public static CouchbaseLiquibaseDatabase createDatabase(CouchbaseContainer container) {
        return new CouchbaseLiquibaseDatabase(new ConnectionData(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        ));
    }

    public static CouchbaseContainer createContainer(String testBucket) {
        String cbVersion = TestPropertyProvider.getProperty("couchbase.version");
        BucketDefinition bucketDef = new BucketDefinition(testBucket).withPrimaryIndex(false);

        return new CouchbaseContainer(CB_IMAGE_NAME.withTag(cbVersion))
                .withBucket(bucketDef)
                .withServiceQuota(CouchbaseService.KV, 512)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .waitingFor(Wait.forHealthcheck());
    }

}

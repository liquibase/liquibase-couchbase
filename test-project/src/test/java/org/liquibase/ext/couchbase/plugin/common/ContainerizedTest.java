package org.liquibase.ext.couchbase.plugin.common;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.operator.ClusterOperator;
import org.testcontainers.couchbase.CouchbaseContainer;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createCouchbaseContainer;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createDatabase;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createJavaMavenContainerToBuildDependency;

public abstract class ContainerizedTest {

    protected static CouchbaseContainer couchbaseContainer;
    protected static Cluster cluster;
    protected static ClusterOperator clusterOperator;

    protected static final String TEST_BUCKET = "testBucket";
    protected static final String TEST_SCOPE = "testScope";
    protected static final String TEST_COLLECTION = "testCollection";

    static {
        couchbaseContainer = createCouchbaseContainer(TEST_BUCKET);
        couchbaseContainer.start();
        CouchbaseLiquibaseDatabase database = createDatabase(couchbaseContainer);

        cluster = database.getConnection().getCluster();
        Bucket bucket = cluster.bucket(TEST_BUCKET);
        bucket.collections().createScope(TEST_SCOPE);
        bucket.collections().createCollection(create(TEST_COLLECTION, TEST_SCOPE));
        clusterOperator = new ClusterOperator(cluster);

        createJavaMavenContainerToBuildDependency();
    }
}

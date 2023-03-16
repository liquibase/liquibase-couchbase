package common

import com.couchbase.client.java.Cluster
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase
import org.testcontainers.couchbase.CouchbaseContainer
import spock.lang.Specification

import static common.ContainerizedTestUtil.createContainer
import static common.ContainerizedTestUtil.createDatabase
import static common.HarnessTestConstants.HARNESS_BUCKET

class HarnessContainerizedSpecification extends Specification {

    protected static final Cluster cluster
    protected static final CouchbaseContainer container
    protected static final CouchbaseLiquibaseDatabase database

    static {
        container = createContainer(HARNESS_BUCKET);
        container.start();
        database = createDatabase(container);
        cluster = database.getConnection().getCluster();
    }

}
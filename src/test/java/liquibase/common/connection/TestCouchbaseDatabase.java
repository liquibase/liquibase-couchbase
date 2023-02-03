package liquibase.common.connection;

import org.testcontainers.couchbase.CouchbaseContainer;

import liquibase.ext.database.ConnectionData;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;

public class TestCouchbaseDatabase extends CouchbaseLiquibaseDatabase {

    public TestCouchbaseDatabase(CouchbaseContainer container) {
        super(new ConnectionData(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        ));
    }

}

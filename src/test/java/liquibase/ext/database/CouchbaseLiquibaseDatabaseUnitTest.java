package liquibase.ext.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CouchbaseLiquibaseDatabaseUnitTest {

    @Test
    void shouldReturnCouchbaseDriverForCouchbaseUrl() {
        CouchbaseLiquibaseDatabase db = new CouchbaseLiquibaseDatabase();
        String url = "couchbase://user@localhost:8091";
        String result = db.getDefaultDriver(url);
        assertEquals(CouchbaseClientDriver.class.getName(), result);
    }
    @Test
    void shouldReturnCouchbaseDriverForCouchbaseSslUrl() {
        CouchbaseLiquibaseDatabase db = new CouchbaseLiquibaseDatabase();
        String sslUrl = "couchbases://user@localhost:8091";
        String result = db.getDefaultDriver(sslUrl);
        assertEquals(CouchbaseClientDriver.class.getName(), result);
    }
}
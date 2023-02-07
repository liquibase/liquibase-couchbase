package liquibase.ext.couchbase.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouchbaseLiquibaseDatabaseTest {

    private static final String driverName = CouchbaseClientDriver.class.getName();

    @Test
    @DisplayName("Returns Couchbase driver name for correct url")
    void shouldReturnCouchbaseDriverForCouchbaseUrl() {
        CouchbaseLiquibaseDatabase db = new CouchbaseLiquibaseDatabase(null);
        String url = "couchbase://user@localhost:8091";

        String result = db.getDefaultDriver(url);

        assertThat(result).isEqualTo(driverName);
    }

    @Test
    @DisplayName("Returns Couchbase driver name for correct ssl url")
    void shouldReturnCouchbaseDriverForCouchbaseSslUrl() {
        CouchbaseLiquibaseDatabase db = new CouchbaseLiquibaseDatabase(null);
        String sslUrl = "couchbases://user@localhost:8091";

        String result = db.getDefaultDriver(sslUrl);

        assertThat(result).isEqualTo(driverName);
    }
}
package liquibase.integration.database;

import liquibase.exception.DatabaseException;
import liquibase.ext.database.CouchbaseClientDriver;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.integration.CouchbaseContainerizedTest;
import org.junit.jupiter.api.Test;

import java.sql.Driver;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CouchbaseConnectionTest extends CouchbaseContainerizedTest {


    @Test
    void shouldOpen() throws DatabaseException {
        Properties properties = new Properties();
        properties.put("user", container.getUsername());
        properties.put("password", container.getPassword());
        String url = container.getConnectionString();
        Driver driver = new CouchbaseClientDriver();

        CouchbaseConnection conn = new CouchbaseConnection();
        conn.open(url, driver, properties);
        assertFalse(conn.isClosed());
    }

}
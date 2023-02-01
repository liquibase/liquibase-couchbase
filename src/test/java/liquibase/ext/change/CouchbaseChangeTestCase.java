package liquibase.ext.change;

import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import org.junit.jupiter.api.BeforeEach;

public abstract class CouchbaseChangeTestCase {

    protected CouchbaseLiquibaseDatabase database;

    @BeforeEach
    void setUp() {
        database = new CouchbaseLiquibaseDatabase();
    }
}

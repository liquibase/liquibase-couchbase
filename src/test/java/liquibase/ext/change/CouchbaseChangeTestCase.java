package liquibase.ext.change;

import org.junit.jupiter.api.BeforeEach;
import liquibase.ext.changelog.ChangeLogProvider;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;

public abstract class CouchbaseChangeTestCase {

    protected CouchbaseLiquibaseDatabase database;
    protected ChangeLogProvider changeLogProvider;

    @BeforeEach
    void setUp() {
        database = new CouchbaseLiquibaseDatabase();
        changeLogProvider = new TestChangeLogProvider(database);
    }
}

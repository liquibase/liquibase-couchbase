package org.liquibase.ext.change;

import org.junit.jupiter.api.BeforeEach;
import org.liquibase.ext.database.CouchbaseLiquibaseDatabase;

public abstract class CouchbaseChangeTestCase {

    protected CouchbaseLiquibaseDatabase database;

    @BeforeEach
    void setUp() {
        database = new CouchbaseLiquibaseDatabase();
    }
}

package common.matchers;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.operator.ChangeLogOperator;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.util.List;


public class CouchbaseDbAssert extends AbstractAssert<CouchbaseDbAssert, Database> {

    private final ChangeLogOperator changeLogOperator;

    private CouchbaseDbAssert(CouchbaseLiquibaseDatabase database) {
        super(database, CouchbaseDbAssert.class);
        this.changeLogOperator = new ChangeLogOperator(database);
    }


    public static CouchbaseDbAssert assertThat(@NonNull CouchbaseLiquibaseDatabase database) {
        return new CouchbaseDbAssert(database);
    }

    public CouchbaseDbAssert lastChangeLogHasExecStatus(ChangeSet.ExecType status) {
        List<RanChangeSet> changeLogs = changeLogOperator.getAllChangeLogs();
        RanChangeSet current = changeLogs.get(changeLogs.size() - 1);
        if (status != current.getExecType()) {
            failWithMessage("Changeset with id [%s](status - [%s]) has not expected status[%s] - ",
                    current.getId(),
                    current.getExecType(),
                    status
            );
        }

        return this;
    }

}

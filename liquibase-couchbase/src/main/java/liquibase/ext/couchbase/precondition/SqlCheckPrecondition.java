package liquibase.ext.couchbase.precondition;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.SqlCheckPreconditionException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;

import java.util.List;

/**
 * A precondition that checks if a bucket exists.
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see SqlCheckPreconditionException
 */
@Data
public class SqlCheckPrecondition extends AbstractCouchbasePrecondition {

    private String expectedResultJson;

    private String query;


    @Override
    public String getName() {
        return "sqlPlusPlusCheck";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws SqlCheckPreconditionException {
        if (isQueryHaveExpectedResult((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new SqlCheckPreconditionException(query, expectedResultJson, changeLog, this);
    }

    public boolean isQueryHaveExpectedResult(CouchbaseConnection connection) {
        ClusterOperator operator = new ClusterOperator(connection.getCluster());
        QueryResult result = operator.executeSingleSql(query);
        JsonObject expected = JsonObject.fromJson(expectedResultJson);
        List<JsonObject> rows = result.rowsAsObject();
        return rows.get(0).equals(expected);
    }

}

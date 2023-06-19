package liquibase.ext.couchbase.change;


import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.RemoveDocumentsQueryStatement;
import liquibase.ext.couchbase.statement.RemoveDocumentsSqlQueryStatement;
import liquibase.ext.couchbase.statement.RemoveDocumentsStatement;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Removes document(-s) by id(-s) / id range or by document filter(in 'whereCondition' field only condition need to be provided, e.g.
 * fieldName="test")
 * @link <a href="https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html#insert">Reference documentation</a>
 * @see RemoveDocumentsStatement
 * @see Keyspace
 */

@Getter
@Setter
@DatabaseChange(
        name = "removeDocuments",
        description = "Remove multiple documents from keyspace https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveDocumentsChange extends CouchbaseChange {

    private String bucketName;
    private String scopeName;
    private String collectionName;
    private Set<Id> ids = new HashSet<>();
    private String whereCondition;
    private String sqlPlusPlusQuery;

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents removed from collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[] {createStatement(keyspace)};
    }

    private SqlStatement createStatement(Keyspace keyspace) {
        if (isNotBlank(sqlPlusPlusQuery)) {
            return new RemoveDocumentsSqlQueryStatement(keyspace, ids, sqlPlusPlusQuery);
        }
        if (isNotBlank(whereCondition)) {
            return new RemoveDocumentsQueryStatement(keyspace, ids, whereCondition);
        }
        return new RemoveDocumentsStatement(keyspace, ids);
    }

}

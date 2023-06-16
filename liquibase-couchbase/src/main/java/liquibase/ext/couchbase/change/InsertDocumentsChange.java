package liquibase.ext.couchbase.change;


import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.InsertDocumentsStatement;
import liquibase.ext.couchbase.statement.InsertFileContentStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.ImportFile;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.statement.SqlStatement;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 * Part of change set package. Responsible for inserting multiple documents into a collection.
 * @link <a href="https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html#insert">Reference documentation</a>
 * @see InsertDocumentsStatement
 * @see Keyspace
 */

@Data
@DatabaseChange(
        name = "insertDocuments",
        description = "Inserts multiple documents into a collection https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
public class InsertDocumentsChange extends DocumentsChange {

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        SqlStatement statement = isFileChange()
                ? new InsertFileContentStatement(keyspace, importFile)
                : new InsertDocumentsStatement(keyspace, documents);
        return new SqlStatement[] {statement};
    }

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents inserted into collection %s", collectionName);
    }

    @Builder
    public InsertDocumentsChange(String bucketName, String scopeName, String collectionName,
                                 ImportFile importFile,
                                 List<Document> documents) {
        super(bucketName, scopeName, collectionName, importFile, documents);
    }
}

package liquibase.ext.couchbase.change;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.UpsertDocumentsStatement;
import liquibase.ext.couchbase.statement.UpsertFileContentStatement;
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
 * Part of change set package. Responsible for upserting multiple documents into a collection.
 * @link <a href="https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html#upsert">Reference documentation</a>
 * @see UpsertDocumentsStatement
 * @see Keyspace
 */

@Data
@DatabaseChange(
        name = "upsertDocuments",
        description = "Upserts multiple documents into a collection https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
public class UpsertDocumentsChange extends DocumentsChange {

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents upserted into collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        SqlStatement sqlStatement = isFileChange()
                ? new UpsertFileContentStatement(keyspace, importFile)
                : new UpsertDocumentsStatement(keyspace, documents);

        return new SqlStatement[] {sqlStatement};
    }

    @Builder
    public UpsertDocumentsChange(String bucketName, String scopeName, String collectionName,
                                 ImportFile importFile,
                                 List<Document> documents) {
        super(bucketName, scopeName, collectionName, importFile, documents);
    }
}


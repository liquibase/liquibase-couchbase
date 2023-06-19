package liquibase.ext.couchbase.change;

import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.ImportFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Common part for inserting changes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DocumentsChange extends CouchbaseChange {

    protected String bucketName;
    protected String scopeName;
    protected String collectionName;

    protected ImportFile importFile;
    protected List<Document> documents = new ArrayList<>();

    public boolean isFileChange() {
        return importFile != null;
    }
}

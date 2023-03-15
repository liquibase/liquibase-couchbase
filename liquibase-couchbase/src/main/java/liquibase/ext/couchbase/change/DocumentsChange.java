package liquibase.ext.couchbase.change;

import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Common part for inserting changes
 */
@Data
public abstract class DocumentsChange extends CouchbaseChange {

    protected String bucketName;
    protected String scopeName;
    protected String collectionName;

    protected File file;
    protected List<Document> documents = new ArrayList<>();

    public boolean isFileChange() {
        return file != null;
    }
}

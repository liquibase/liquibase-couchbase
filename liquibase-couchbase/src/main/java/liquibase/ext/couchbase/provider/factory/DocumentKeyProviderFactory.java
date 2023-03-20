package liquibase.ext.couchbase.provider.factory;

import liquibase.SingletonObject;
import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.provider.FieldDocumentKeyProvider;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.KeyProviderType;

/**
 * This is factory of Couchbase document key's providers. It is using to get keys for documents that import from file
 */
public class DocumentKeyProviderFactory implements SingletonObject {

    public DocumentKeyProvider getKeyProvider(File file) {
        if (KeyProviderType.DEFAULT.equals(file.getKeyProviderType())) {
            return new FieldDocumentKeyProvider(file.getKeyProviderExpression());
        }
        return null;
    }
}

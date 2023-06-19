package liquibase.ext.couchbase.provider.factory;

import liquibase.SingletonObject;
import liquibase.ext.couchbase.exception.KeyProviderNotFoundException;
import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.provider.ExpressionDocumentKeyProvider;
import liquibase.ext.couchbase.provider.FieldDocumentKeyProvider;
import liquibase.ext.couchbase.provider.IncrementalDocumentKeyProvider;
import liquibase.ext.couchbase.provider.UidDocumentKeyProvider;
import liquibase.ext.couchbase.types.ImportFile;

/**
 * Factory of Couchbase document key's providers.
 */
public class DocumentKeyProviderFactory implements SingletonObject {

    public DocumentKeyProvider getKeyProvider(ImportFile importFile) {
        switch (importFile.getKeyProviderType()) {
            case DEFAULT:
                return new FieldDocumentKeyProvider(importFile.getKeyProviderExpression());
            case UID:
                return new UidDocumentKeyProvider();
            case INCREMENT:
                return new IncrementalDocumentKeyProvider();
            case EXPRESSION:
                return new ExpressionDocumentKeyProvider(importFile.getKeyProviderExpression());
            default:
                throw new KeyProviderNotFoundException(importFile.getKeyProviderType());
        }
    }
}

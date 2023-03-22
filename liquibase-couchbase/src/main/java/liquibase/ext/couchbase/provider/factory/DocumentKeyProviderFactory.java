package liquibase.ext.couchbase.provider.factory;

import com.google.common.collect.ImmutableMap;
import liquibase.SingletonObject;
import liquibase.ext.couchbase.exception.KeyProviderNotFoundException;
import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.provider.FieldDocumentKeyProvider;
import liquibase.ext.couchbase.provider.UidDocumentKeyProvider;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.KeyProviderType;

import java.util.Map;
import java.util.function.Function;

import static liquibase.ext.couchbase.types.KeyProviderType.DEFAULT;
import static liquibase.ext.couchbase.types.KeyProviderType.UID;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

/**
 * Factory of Couchbase document key's providers.
 */
public class DocumentKeyProviderFactory implements SingletonObject {
    private static Map<KeyProviderType, Function<File, DocumentKeyProvider>> providersMap =
            ImmutableMap.of(DEFAULT, file -> new FieldDocumentKeyProvider(file.getKeyProviderExpression())
                    , UID, file -> new UidDocumentKeyProvider());

    public DocumentKeyProvider getKeyProvider(File file) {
        KeyProviderType keyProviderType = file.getKeyProviderType();
        if (isFalse(providersMap.containsKey(keyProviderType))) {
            throw new KeyProviderNotFoundException(keyProviderType.toString());
        }
        return providersMap.get(keyProviderType).apply(file);
    }
}

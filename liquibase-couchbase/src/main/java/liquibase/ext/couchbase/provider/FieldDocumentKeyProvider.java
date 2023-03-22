package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.exception.ProvideKeyFailedException;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Document's key provider which uses specific field from document as key. If there's no field in document the document consider as
 * incorrect
 */
public class FieldDocumentKeyProvider implements DocumentKeyProvider {
    private static final String DEFAULT_KEY_FIELD = "id";
    private final String keyField;

    public FieldDocumentKeyProvider(String keyField) {
        this.keyField = isEmpty(keyField) ? DEFAULT_KEY_FIELD : keyField;
    }

    @Override
    public String getKey(JsonObject jsonObject) {
        return Optional.of(keyField)
                .filter(jsonObject::containsKey)
                .map(jsonObject::getString)
                .orElseThrow(() -> new ProvideKeyFailedException("Document contains no key field"));
    }
}

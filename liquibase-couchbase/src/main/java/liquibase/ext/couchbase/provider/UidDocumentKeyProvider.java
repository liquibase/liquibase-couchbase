package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.provider.generator.UidKeyGenerator;
import lombok.RequiredArgsConstructor;

/**
 * UID document key provider. Generates random UID for every call
 */
@RequiredArgsConstructor
public class UidDocumentKeyProvider implements DocumentKeyProvider {
    private final UidKeyGenerator generator;

    public UidDocumentKeyProvider() {
        this(new UidKeyGenerator());
    }

    @Override
    public String getKey(JsonObject jsonObject) {
        return generator.generate();
    }
}

package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.provider.generator.UidKeyGenerator;

/**
 * UID document key provider. Generates random UID for every call
 */
public class UidDocumentKeyProvider implements DocumentKeyProvider {
    private final UidKeyGenerator generator;

    public UidDocumentKeyProvider() {
        this.generator = new UidKeyGenerator();
    }

    @Override
    public String getKey(JsonObject jsonObject) {
        return generator.generate();
    }
}

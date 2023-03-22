package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.provider.generator.IncrementalKeyGenerator;

/**
 * Incremental document key provider. Generates auto incremented number for every call
 */
public class IncrementalDocumentKeyProvider implements DocumentKeyProvider {

    private final IncrementalKeyGenerator generator;

    public IncrementalDocumentKeyProvider() {
        this.generator = new IncrementalKeyGenerator();
    }

    @Override
    public String getKey(JsonObject jsonObject) {
        return generator.generate();
    }
}

package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;

import java.util.UUID;

/**
 * UID document key provider. Generates random UID for every call
 */
public class UidDocumentKeyProvider implements DocumentKeyProvider {

    @Override
    public String getKey(JsonObject jsonObject) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}

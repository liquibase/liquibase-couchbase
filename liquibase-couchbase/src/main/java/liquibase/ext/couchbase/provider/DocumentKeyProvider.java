package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;

/**
 * Document key provider interface. Generate key as string using document content if applicable
 */
public interface DocumentKeyProvider {

    String getKey(JsonObject object);
}

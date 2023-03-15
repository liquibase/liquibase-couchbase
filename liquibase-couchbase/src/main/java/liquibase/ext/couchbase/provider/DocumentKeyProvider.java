package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;

/**
 * Document key provider interface. Generate key as string from object which is document content
 */
public interface DocumentKeyProvider {

    String getKey(JsonObject object);
}

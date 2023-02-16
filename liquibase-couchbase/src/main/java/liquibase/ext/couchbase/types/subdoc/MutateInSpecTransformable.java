package liquibase.ext.couchbase.types.subdoc;

import com.couchbase.client.java.kv.MutateInSpec;

public interface MutateInSpecTransformable {
    MutateInSpec toSpec();
}

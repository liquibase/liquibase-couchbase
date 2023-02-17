package liquibase.ext.couchbase.types.subdoc;

import com.couchbase.client.java.kv.MutateInSpec;

import java.util.List;

import liquibase.ext.couchbase.types.Keyspace;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents data necessary to build {@link MutateInSpec}
 *
 * @see Keyspace
 * @see MutateInSpec
 */
@Data
@Builder
@RequiredArgsConstructor
public class MutateIn {

    private final String id;
    private final Keyspace keyspace;
    private final List<MutateInSpec> specs;

}

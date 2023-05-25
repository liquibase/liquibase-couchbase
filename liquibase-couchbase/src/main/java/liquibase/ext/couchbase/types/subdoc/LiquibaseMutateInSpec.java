package liquibase.ext.couchbase.types.subdoc;

import com.couchbase.client.java.kv.MutateInSpec;
import liquibase.ext.couchbase.types.Value;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a ${@link MutateInSpec}
 * @see MutateInSpec
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquibaseMutateInSpec extends AbstractLiquibaseSerializable {

    private String path;
    private List<Value> values = new ArrayList<>();
    private MutateInType mutateInType;

    @Override
    public String getSerializedObjectName() {
        return "mutateInSpec";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

}

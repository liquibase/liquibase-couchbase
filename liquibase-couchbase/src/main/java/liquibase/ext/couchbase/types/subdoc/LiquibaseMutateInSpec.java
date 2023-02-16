package liquibase.ext.couchbase.types.subdoc;

import com.couchbase.client.java.kv.MutateInSpec;

import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * Represents a ${@link MutateInSpec}
 *
 * @see MutateInSpec
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquibaseMutateInSpec extends AbstractLiquibaseSerializable implements MutateInSpecTransformable {

    private String path;
    private String value;
    private MutateInType mutateInType;

    @Override
    public MutateInSpec toSpec() {
        return mutateInType.toMutateInSpec(path, value);
    }

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

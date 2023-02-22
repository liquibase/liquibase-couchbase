package liquibase.ext.couchbase.types;

import liquibase.ext.couchbase.exception.MutateInTypeUnsupportedException;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document's value(s). Used in MutateIn
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Value extends AbstractLiquibaseSerializable {

    private String data;
    private DataType type;

    @Override
    public String getSerializedObjectName() {
        return "value";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

    public void setType(String type) {
        this.type = DataType.valueOf(type.toUpperCase());
    }

    public Object mapDataToType() {
        switch (type) {
            case LONG:
                return Long.valueOf(data);
            case DOUBLE:
                return Double.valueOf(data);
            case BOOLEAN:
                return Boolean.valueOf(data);
            case STRING:
                return data;
            default:
                throw new MutateInTypeUnsupportedException(type);
        }
    }

}

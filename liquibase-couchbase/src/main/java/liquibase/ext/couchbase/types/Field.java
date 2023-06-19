package liquibase.ext.couchbase.types;

import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Document field name, frequently used in indexes
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 * @see liquibase.ext.couchbase.statement.CreatePrimaryQueryIndexStatement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field extends AbstractLiquibaseSerializable {

    @SuppressWarnings("java:S1700") // This is a requirement from Liquibase to have type with field named as classname
    private String field;

    @Override
    public String getSerializedObjectName() {
        return "field";
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

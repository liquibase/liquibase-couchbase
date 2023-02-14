package liquibase.ext.couchbase.types;

import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents Couchbase document, contain it's key and json value
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document extends AbstractLiquibaseSerializable {
    private String id;
    private String content;

    @Override
    public String getSerializedObjectName() {
        return "document";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    public static Document document(String id, String content) {
        return new Document(id, content);
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }
}

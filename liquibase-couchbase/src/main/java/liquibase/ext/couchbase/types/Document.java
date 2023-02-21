package liquibase.ext.couchbase.types;

import com.couchbase.client.java.json.JsonObject;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Couchbase document, contain it's key and JSON value
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
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

    public static Document document(String id, JsonObject content) {
        return new Document(id, content.toString());
    }

    public JsonObject getContentAsObject() {
        return JsonObject.fromJson(content);
    }

    public List<Field> getFields() {
        return getContentAsObject().getNames().stream().map(Field::new).collect(Collectors.toList());
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

}

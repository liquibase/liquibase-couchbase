package liquibase.ext.couchbase.types;

import com.couchbase.client.java.json.JsonObject;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document extends AbstractLiquibaseSerializable {

    private String id;
    private Value value = new Value();

    @Override
    public String getSerializedObjectName() {
        return "document";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    public static Document document(String id, String content, DataType type) {
        return Document.builder().id(id).value(new Value(content, type)).build();
    }

    public static Document document(String id, Value value) {
        return Document.builder().id(id).value(value).build();
    }

    public Object getContentAsObject() {
        return value.mapDataToType();
    }

    public JsonObject getContentAsJson() {
        return (JsonObject) getContentAsObject();
    }

    public List<Field> getFields() {
        return getContentAsJson().getNames().stream().map(Field::new).collect(Collectors.toList());
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

}

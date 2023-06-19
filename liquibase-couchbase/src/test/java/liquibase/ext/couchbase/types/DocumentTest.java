package liquibase.ext.couchbase.types;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static liquibase.serializer.LiquibaseSerializable.SerializationType.DIRECT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentTest {

    private final String id = "id";
    private final Value value = new Value("{\"value\": 1}", DataType.JSON);

    private final Document document = Document.document(id, value);

    @Test
    void Should_return_expected_fields() {
        List<Field> fields = new ArrayList<>();
        fields.add(new Field("value"));
        assertThat(document.getFields()).isEqualTo(fields);
    }

    @Test
    void Should_return_expected_serialized_object_name() {
        assertThat(document.getSerializedObjectName()).isEqualTo("document");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        assertThat(document.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }

    @Test
    void Should_return_expected_serialized_field_type() {
        assertThat(document.getSerializableFieldType(null)).isEqualTo(DIRECT_VALUE);
    }
}

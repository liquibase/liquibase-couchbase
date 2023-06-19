package liquibase.ext.couchbase.types;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import org.junit.jupiter.api.Test;

import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static liquibase.serializer.LiquibaseSerializable.SerializationType.DIRECT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

class ValueTest {

    @Test
    void Should_map_to_long() {
        Value value = new Value("1", DataType.LONG);

        assertThat(value.mapDataToType()).isEqualTo(1L);
    }

    @Test
    void Should_map_to_double() {
        Value value = new Value("1.0", DataType.DOUBLE);

        assertThat(value.mapDataToType()).isEqualTo(1.0);
    }

    @Test
    void Should_map_to_boolean() {
        Value value = new Value("TRUE", DataType.BOOLEAN);

        assertThat(value.mapDataToType()).isEqualTo(Boolean.TRUE);
    }

    @Test
    void Should_map_to_string() {
        Value value = new Value("abc", DataType.STRING);

        assertThat(value.mapDataToType()).isEqualTo("abc");
    }

    @Test
    void Should_map_to_json() {
        String data = "{\"value\": 1}";
        Value value = new Value(data, DataType.JSON);
        JsonObject expected = JsonObject.fromJson(data);
        assertThat(value.mapDataToType()).isEqualTo(expected);
    }

    @Test
    void Should_map_to_json_array() {
        String data = "[1, 2, 3, 4]";
        Value value = new Value(data, DataType.JSON_ARRAY);
        JsonArray expected = JsonArray.fromJson(data);
        assertThat(value.mapDataToType()).isEqualTo(expected);
    }

    @Test
    void Should_return_expected_serialized_object_name() {
        Value value = new Value();

        assertThat(value.getSerializedObjectName()).isEqualTo("value");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        Value value = new Value();

        assertThat(value.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }

    @Test
    void Should_return_expected_serialized_field_type() {
        Value value = new Value();

        assertThat(value.getSerializableFieldType(null)).isEqualTo(DIRECT_VALUE);
    }
}

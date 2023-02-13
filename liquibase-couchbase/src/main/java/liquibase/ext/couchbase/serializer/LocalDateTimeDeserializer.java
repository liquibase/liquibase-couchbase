package liquibase.ext.couchbase.serializer;

import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonParser;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.DeserializationContext;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser date, DeserializationContext context) throws IOException {
        return LocalDateTime.parse(date.getText());
    }
}

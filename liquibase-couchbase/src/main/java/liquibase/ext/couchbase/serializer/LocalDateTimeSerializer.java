package liquibase.ext.couchbase.serializer;

import com.couchbase.client.core.deps.com.fasterxml.jackson.core.JsonGenerator;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.JsonSerializer;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime date, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(date.toString());
    }
}

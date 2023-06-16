package liquibase.ext.couchbase.types;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.ext.couchbase.exception.IncorrectFileException;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * File to import
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImportFile extends AbstractLiquibaseSerializable {

    private File file;
    private ImportType importType;
    private KeyProviderType keyProviderType;
    private String keyProviderExpression;

    @Override
    public String getSerializedObjectName() {
        return "importFile";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

    public Stream<String> lines() {
        try {
            return Files.lines(Paths.get(file.getFilePath()));
        } catch (IOException e) {
            throw new IncorrectFileException(file.getFilePath());
        }
    }

    public List<Map<String, Object>> readJsonList() {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readerForListOf(Map.class).readValue(Paths.get(file.getFilePath()).toFile());
        } catch (IOException ex) {
            throw new IncorrectFileException(file.getFilePath());
        }
    }

}

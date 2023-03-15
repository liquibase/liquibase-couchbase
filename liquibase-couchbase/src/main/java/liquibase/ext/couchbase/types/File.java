package liquibase.ext.couchbase.types;

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
public class File extends AbstractLiquibaseSerializable {

    private String filePath;
    private ImportType importType;
    private KeyProviderType keyProviderType;
    private String keyProviderExpression;

    @Override
    public String getSerializedObjectName() {
        return "file";
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
            return Files.lines(Paths.get(filePath));
        } catch (IOException e) {
            throw new IncorrectFileException(filePath);
        }
    }
}

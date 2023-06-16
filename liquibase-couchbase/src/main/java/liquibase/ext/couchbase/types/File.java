package liquibase.ext.couchbase.types;

import liquibase.ext.couchbase.exception.IncorrectFileException;
import liquibase.resource.Resource;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.IOException;

import static liquibase.Scope.getCurrentScope;

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

    private Boolean relative;
    private String filePath;

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

    public Resource getAsResource(String changeSetPath) {
        try {
            ResourceAccessor resourceAccessor = getCurrentScope().getResourceAccessor();
            return (relative != null && relative)
                    ? resourceAccessor.get(changeSetPath).resolveSibling(getFilePath())
                    : resourceAccessor.get(getFilePath());
        } catch (IOException ex) {
            throw new IncorrectFileException(getFilePath());
        }
    }
}

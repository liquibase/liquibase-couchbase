package liquibase.ext.couchbase.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import liquibase.Scope;
import liquibase.SingletonObject;
import liquibase.ext.couchbase.exception.InvalidJSONException;
import liquibase.ext.couchbase.exception.ResourceNotFoundException;
import liquibase.logging.Logger;
import liquibase.resource.Resource;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Set;

public class JsonChangelogValidator implements SingletonObject {

    private final Logger log = Scope.getCurrentScope().getLog(getClass());
    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory;

    public JsonChangelogValidator() {
        this(new ObjectMapper(), JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909));
    }

    @VisibleForTesting
    JsonChangelogValidator(ObjectMapper objectMapper, JsonSchemaFactory schemaFactory) {
        this.objectMapper = objectMapper;
        this.schemaFactory = schemaFactory;
    }

    @SneakyThrows
    public void validateChangeLogFile(String fileLocation, String schemaLocation) {
        Resource jsonChange = load(fileLocation);
        Resource jsonSchema = load(schemaLocation);
        validateBySchema(jsonChange, jsonSchema);
    }

    @SneakyThrows
    private Resource load(String filePhysicalLocation) {
        ResourceAccessor resourceAccessor = Scope.getCurrentScope().getResourceAccessor();
        Resource resource = resourceAccessor.get(filePhysicalLocation);
        if (!resource.exists()) {
            throw new ResourceNotFoundException(filePhysicalLocation);
        }
        return resource;
    }

    @SneakyThrows
    private void validateBySchema(Resource jsonChange, Resource jsonSchema) {
        try (InputStream changeStream = jsonChange.openInputStream();
             InputStream schemaStream = jsonSchema.openInputStream()) {
            Set<ValidationMessage> validationResult = doValidate(changeStream, schemaStream);
            if (validationResult.isEmpty()) {
                log.info("Json changelog validated successfully");
                return;
            }
            validationResult.forEach(vm -> log.warning(vm.getMessage()));
            throw new InvalidJSONException(jsonChange.getPath());
        }
    }

    @SneakyThrows
    private Set<ValidationMessage> doValidate(InputStream changeIS, InputStream schemaIS) {
        JsonNode json = objectMapper.readTree(changeIS);
        JsonSchema schema = schemaFactory.getSchema(schemaIS);
        return schema.validate(json);
    }
}

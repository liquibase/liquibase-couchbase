package liquibase.ext.couchbase.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ErrorMessageType;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.ValidatorTypeCode;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.InvalidJSONException;
import liquibase.ext.couchbase.exception.ResourceNotFoundException;
import liquibase.resource.Resource;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class JsonChangelogValidatorTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JsonSchemaFactory schemaFactory;
    @InjectMocks
    private JsonChangelogValidator validator;

    private Scope scope = mock(Scope.class);
    private ResourceAccessor resourceAccessor = mock(ResourceAccessor.class);
    private Resource changelogResource = mock(Resource.class);
    private Resource schemaResource = mock(Resource.class);
    private InputStream changelogInputStream = mock(InputStream.class);
    private InputStream schemaInputStream = mock(InputStream.class);
    private JsonNode jsonNode = mock(JsonNode.class);
    private JsonSchema jsonSchema = mock(JsonSchema.class);

    @Test
    @SneakyThrows
    void Should_validate_json_by_schema_successfully() {
        String pathToChangelog = "pathToChangelog";
        String pathToSchema = "pathToSchema";

        try (MockedStatic<Scope> scopeUtilities = Mockito.mockStatic(Scope.class)) {
            scopeUtilities.when(Scope::getCurrentScope)
                    .thenReturn(scope);

            when(scope.getResourceAccessor()).thenReturn(resourceAccessor);
            when(resourceAccessor.get(pathToChangelog)).thenReturn(changelogResource);
            when(resourceAccessor.get(pathToSchema)).thenReturn(schemaResource);
            when(changelogResource.exists()).thenReturn(true);
            when(schemaResource.exists()).thenReturn(true);
            when(changelogResource.openInputStream()).thenReturn(changelogInputStream);
            when(schemaResource.openInputStream()).thenReturn(schemaInputStream);

            when(objectMapper.readTree(changelogInputStream)).thenReturn(jsonNode);
            when(schemaFactory.getSchema(schemaInputStream)).thenReturn(jsonSchema);
            when(jsonSchema.validate(jsonNode)).thenReturn(emptySet());

            assertDoesNotThrow(() -> validator.validateChangeLogFile(pathToChangelog, pathToSchema));
        }
    }

    @Test
    @SneakyThrows
    void Should_throw_resource_not_found_exception_when_resource_not_found() {
        String pathToChangelog = "pathToChangelog";
        String pathToSchema = "pathToSchema";

        try (MockedStatic<Scope> scopeUtilities = Mockito.mockStatic(Scope.class)) {
            scopeUtilities.when(Scope::getCurrentScope)
                    .thenReturn(scope);

            when(scope.getResourceAccessor()).thenReturn(resourceAccessor);
            when(resourceAccessor.get(pathToChangelog)).thenReturn(changelogResource);
            when(resourceAccessor.get(pathToSchema)).thenReturn(schemaResource);

            assertThrows(ResourceNotFoundException.class, () -> validator.validateChangeLogFile(pathToChangelog, pathToSchema));
        }
    }

    @Test
    @SneakyThrows
    void Should_throw_invalid_json_exception_when_validation_error_messages() {
        String pathToChangelog = "pathToChangelog";
        String pathToSchema = "pathToSchema";

        ValidationMessage validationMessage = prepareValidationMessage();
        Set<ValidationMessage> validationResult = new HashSet<>();
        validationResult.add(validationMessage);

        try (MockedStatic<Scope> scopeUtilities = Mockito.mockStatic(Scope.class)) {
            scopeUtilities.when(Scope::getCurrentScope)
                    .thenReturn(scope);

            when(scope.getResourceAccessor()).thenReturn(resourceAccessor);
            when(resourceAccessor.get(pathToChangelog)).thenReturn(changelogResource);
            when(resourceAccessor.get(pathToSchema)).thenReturn(schemaResource);
            when(changelogResource.exists()).thenReturn(true);
            when(schemaResource.exists()).thenReturn(true);
            when(changelogResource.openInputStream()).thenReturn(changelogInputStream);
            when(schemaResource.openInputStream()).thenReturn(schemaInputStream);

            when(objectMapper.readTree(changelogInputStream)).thenReturn(jsonNode);
            when(schemaFactory.getSchema(schemaInputStream)).thenReturn(jsonSchema);
            when(jsonSchema.validate(jsonNode)).thenReturn(validationResult);

            assertThrows(InvalidJSONException.class, () -> validator.validateChangeLogFile(pathToChangelog, pathToSchema));
        }
    }

    private ValidationMessage prepareValidationMessage() {
        ErrorMessageType errorMessageType = ValidatorTypeCode.NOT_ALLOWED;
        return ValidationMessage
                .of("some type", errorMessageType, "at", "path", "argument");
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_error_during_validation() {
        String pathToChangelog = "pathToChangelog";
        String pathToSchema = "pathToSchema";


        try (MockedStatic<Scope> scopeUtilities = Mockito.mockStatic(Scope.class)) {
            scopeUtilities.when(Scope::getCurrentScope)
                    .thenReturn(scope);

            when(scope.getResourceAccessor()).thenReturn(resourceAccessor);
            when(resourceAccessor.get(pathToChangelog)).thenReturn(changelogResource);
            when(resourceAccessor.get(pathToSchema)).thenReturn(schemaResource);
            when(changelogResource.exists()).thenReturn(true);
            when(schemaResource.exists()).thenReturn(true);
            when(changelogResource.openInputStream()).thenReturn(changelogInputStream);
            when(schemaResource.openInputStream()).thenReturn(schemaInputStream);

            when(objectMapper.readTree(changelogInputStream)).thenReturn(jsonNode);
            when(schemaFactory.getSchema(schemaInputStream)).thenReturn(jsonSchema);
            when(jsonSchema.validate(jsonNode)).thenThrow(RuntimeException.class);

            assertThrows(RuntimeException.class, () -> validator.validateChangeLogFile(pathToChangelog, pathToSchema));
        }
    }

    @Test
    @SneakyThrows
    void Should_create_constructor_with_default_parameters() {
        new JsonChangelogValidator();
    }

}

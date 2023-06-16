package liquibase.ext.couchbase.types;

import liquibase.Scope;
import liquibase.ext.couchbase.exception.IncorrectFileException;
import liquibase.resource.Resource;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;

import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static liquibase.serializer.LiquibaseSerializable.SerializationType.DIRECT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class FileTest {

    private static final String FILE_PATH = "filePath";

    @Mock
    private Scope scope;
    @Mock
    private ResourceAccessor resourceAccessor;
    @Mock
    private Resource resource;

    private final File file = File.builder()
            .relative(false)
            .filePath(FILE_PATH)
            .build();

    @Test
    void Should_return_expected_serialized_object_name() {
        assertThat(file.getSerializedObjectName()).isEqualTo("file");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        assertThat(file.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }

    @Test
    void Should_return_expected_serialized_field_type() {
        assertThat(file.getSerializableFieldType(null)).isEqualTo(DIRECT_VALUE);
    }

    @Test
    @SneakyThrows
    void Should_return_resource_by_file_path() {
        try (MockedStatic<Scope> mockedStaticScope = Mockito.mockStatic(Scope.class)) {
            mockedStaticScope.when(Scope::getCurrentScope).thenReturn(scope);

            when(scope.getResourceAccessor()).thenReturn(resourceAccessor);
            when(resourceAccessor.get(FILE_PATH)).thenReturn(resource);

            Resource result = file.getAsResource(null);

            assertThat(result).isEqualTo(resource);

            verify(resourceAccessor).get(FILE_PATH);
        }
    }

    @Test
    @SneakyThrows
    void Should_return_relative_resource_by_file_path() {
        String changeSetPath = "C";
        File relativeFile = File.builder()
                .relative(true)
                .filePath(FILE_PATH)
                .build();
        try (MockedStatic<Scope> mockedStaticScope = Mockito.mockStatic(Scope.class)) {
            mockedStaticScope.when(Scope::getCurrentScope).thenReturn(scope);

            when(scope.getResourceAccessor()).thenReturn(resourceAccessor);
            when(resourceAccessor.get(changeSetPath)).thenReturn(resource);
            when(resource.resolveSibling(FILE_PATH)).thenReturn(resource);

            Resource result = relativeFile.getAsResource(changeSetPath);

            assertThat(result).isEqualTo(resource);

            verify(resourceAccessor).get(changeSetPath);
            verify(resource).resolveSibling(FILE_PATH);
        }
    }

    @Test
    @SneakyThrows
    void Should_catch_exception_if_file_missing() {
        try (MockedStatic<Scope> mockedStaticScope = Mockito.mockStatic(Scope.class)) {
            mockedStaticScope.when(Scope::getCurrentScope).thenReturn(scope);

            when(scope.getResourceAccessor()).thenReturn(resourceAccessor);
            when(resourceAccessor.get(FILE_PATH)).thenThrow(new IOException());

            assertThatExceptionOfType(IncorrectFileException.class)
                    .isThrownBy(() -> file.getAsResource(null))
                    .withMessageContaining("File [%s] format incorrect", FILE_PATH);

            verify(resourceAccessor).get(FILE_PATH);
        }
    }

}

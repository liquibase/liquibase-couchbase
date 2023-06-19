package liquibase.ext.couchbase.types;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectReader;
import liquibase.ext.couchbase.exception.IncorrectFileException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static liquibase.serializer.LiquibaseSerializable.SerializationType.DIRECT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings
class ImportFileTest {

    private static final String FILE_PATH = "filePath";

    @Mock
    private ObjectReader reader;
    @Mock
    private Path path;

    @Mock
    private File file;

    @InjectMocks
    private ImportFile importFile;

    @Test
    void Should_return_expected_serialized_object_name() {
        assertThat(importFile.getSerializedObjectName()).isEqualTo("importFile");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        assertThat(importFile.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }

    @Test
    void Should_return_expected_serialized_field_type() {
        assertThat(importFile.getSerializableFieldType(null)).isEqualTo(DIRECT_VALUE);
    }

    @Test
    void Should_return_expected_lines() {
        when(file.getFilePath()).thenReturn(FILE_PATH);

        List<String> expected = new ArrayList<>();
        expected.add("test1");
        expected.add("test2");

        try (MockedStatic<Paths> mockedStaticPaths = Mockito.mockStatic(Paths.class)) {
            mockedStaticPaths.when(() -> Paths.get(anyString())).thenReturn(path);

            try (MockedStatic<Files> mockedStaticFiles = Mockito.mockStatic(Files.class)) {
                mockedStaticFiles.when(() -> Files.lines(any())).thenReturn(expected.stream());

                Stream<String> result = importFile.lines();

                assertThat(result).isNotNull();

                List<String> resultList = result.collect(toList());

                assertThat(resultList).isEqualTo(expected);

                mockedStaticFiles.verify(() -> Files.lines(eq(path)));
            }
            mockedStaticPaths.verify(() -> Paths.get(FILE_PATH));
        }
    }

    @Test
    void Should_wrap_exception_on_invalid_file_on_lines() {
        when(file.getFilePath()).thenReturn(FILE_PATH);

        try (MockedStatic<Paths> mockedStaticPaths = Mockito.mockStatic(Paths.class)) {
            mockedStaticPaths.when(() -> Paths.get(anyString())).thenReturn(path);
            try (MockedStatic<Files> mockedStaticFiles = Mockito.mockStatic(Files.class)) {
                mockedStaticFiles.when(() -> Files.lines(any())).thenThrow(new IOException("Mocked"));

                assertThatExceptionOfType(IncorrectFileException.class)
                        .isThrownBy(importFile::lines)
                        .withMessage("File [%s] format incorrect", FILE_PATH);
            }
        }
    }

    @Test
    void Should_return_expected_on_readJsonList() throws IOException {
        when(file.getFilePath()).thenReturn(FILE_PATH);

        java.io.File mockedFile = mock(java.io.File.class);
        List<Map<String, Object>> expected = new ArrayList<>();
        Map<String, Object> value = new HashMap<>();
        value.put("test", 1L);
        expected.add(value);
        try (MockedStatic<Paths> mockedStaticPaths = Mockito.mockStatic(Paths.class)) {
            mockedStaticPaths.when(() -> Paths.get(anyString())).thenReturn(path);
            when(path.toFile()).thenReturn(mockedFile);

            when(reader.readValue(mockedFile)).thenReturn(expected);

            try (MockedConstruction<ObjectMapper> ignored = Mockito.mockConstruction(ObjectMapper.class,
                    (mapper, context) -> when(mapper.readerForListOf(Map.class)).thenReturn(reader))) {
                List<Map<String, Object>> result = importFile.readJsonList();
                assertThat(result).isEqualTo(expected);
            }
        }
    }

    @Test
    void Should_wrap_exception_on_invalid_file_on_readJsonList() throws IOException {
        when(file.getFilePath()).thenReturn(FILE_PATH);

        java.io.File mockedFile = mock(java.io.File.class);
        try (MockedStatic<Paths> mockedStaticPaths = Mockito.mockStatic(Paths.class)) {
            mockedStaticPaths.when(() -> Paths.get(anyString())).thenReturn(path);
            when(path.toFile()).thenReturn(mockedFile);

            when(reader.readValue(mockedFile)).thenThrow(new IOException("Mocked"));

            try (MockedConstruction<ObjectMapper> ignored = Mockito.mockConstruction(ObjectMapper.class,
                    (mapper, context) -> when(mapper.readerForListOf(Map.class)).thenReturn(reader))) {

                assertThatExceptionOfType(IncorrectFileException.class)
                        .isThrownBy(importFile::readJsonList)
                        .withMessage("File [%s] format incorrect", FILE_PATH);
            }
        }
    }
}

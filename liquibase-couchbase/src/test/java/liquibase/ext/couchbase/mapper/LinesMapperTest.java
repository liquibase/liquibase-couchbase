package liquibase.ext.couchbase.mapper;

import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.provider.factory.DocumentKeyProviderFactory;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.Value;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings
class LinesMapperTest {

    @Mock
    private DocumentKeyProviderFactory documentKeyProviderFactory;
    @Mock
    private DocumentKeyProvider documentKeyProvider;
    @Mock
    private File file;

    private final AtomicLong keyHolder = new AtomicLong(1L);

    @InjectMocks
    private LinesMapper linesMapper;

    @Test
    void Should_map_file_successfully() {
        when(file.lines()).thenReturn(Stream.of("{}", "{}", "{}"));
        when(documentKeyProviderFactory.getKeyProvider(any())).thenReturn(documentKeyProvider);
        when(documentKeyProvider.getKey(any())).thenAnswer((args) -> String.valueOf(keyHolder.getAndIncrement()));

        List<Document> expected = createDocuments();
        List<Document> result = linesMapper.map(file);
        assertThat(result).isEqualTo(expected);

        verify(documentKeyProviderFactory).getKeyProvider(any());
        verify(documentKeyProvider, times(3)).getKey(any());
    }

    private List<Document> createDocuments() {
        List<Document> documentList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            documentList.add(new Document(String.valueOf(keyHolder.get() + i), new Value("{}", DataType.JSON)));
        }
        return documentList;
    }
}

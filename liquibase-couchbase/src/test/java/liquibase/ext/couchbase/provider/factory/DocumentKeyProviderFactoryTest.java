package liquibase.ext.couchbase.provider.factory;

import liquibase.ext.couchbase.provider.ExpressionDocumentKeyProvider;
import liquibase.ext.couchbase.provider.FieldDocumentKeyProvider;
import liquibase.ext.couchbase.provider.IncrementalDocumentKeyProvider;
import liquibase.ext.couchbase.provider.UidDocumentKeyProvider;
import liquibase.ext.couchbase.types.ImportFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static liquibase.ext.couchbase.types.KeyProviderType.DEFAULT;
import static liquibase.ext.couchbase.types.KeyProviderType.EXPRESSION;
import static liquibase.ext.couchbase.types.KeyProviderType.INCREMENT;
import static liquibase.ext.couchbase.types.KeyProviderType.UID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings
class DocumentKeyProviderFactoryTest {

    @Mock
    private final ImportFile importFile = mock(ImportFile.class);
    private final DocumentKeyProviderFactory documentKeyProviderFactory = new DocumentKeyProviderFactory();

    @Test
    void Should_return_default() {
        when(importFile.getKeyProviderType()).thenReturn(DEFAULT);

        assertThat(documentKeyProviderFactory.getKeyProvider(importFile)).isInstanceOf(FieldDocumentKeyProvider.class);
    }

    @Test
    void Should_return_uid() {
        when(importFile.getKeyProviderType()).thenReturn(UID);

        assertThat(documentKeyProviderFactory.getKeyProvider(importFile)).isInstanceOf(UidDocumentKeyProvider.class);
    }

    @Test
    void Should_return_incremental() {
        when(importFile.getKeyProviderType()).thenReturn(INCREMENT);

        assertThat(documentKeyProviderFactory.getKeyProvider(importFile)).isInstanceOf(IncrementalDocumentKeyProvider.class);
    }

    @Test
    void Should_return_expression() {
        when(importFile.getKeyProviderType()).thenReturn(EXPRESSION);
        when(importFile.getKeyProviderExpression()).thenReturn("#a, #b");

        assertThat(documentKeyProviderFactory.getKeyProvider(importFile)).isInstanceOf(ExpressionDocumentKeyProvider.class);
    }

}

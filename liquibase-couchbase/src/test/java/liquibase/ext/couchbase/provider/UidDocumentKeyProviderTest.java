package liquibase.ext.couchbase.provider;

import liquibase.ext.couchbase.provider.generator.UidKeyGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class UidDocumentKeyProviderTest {

    @Mock
    private UidKeyGenerator uidKeyGenerator;

    @Test
    void Should_call_generate() {
        UidDocumentKeyProvider uidDocumentKeyProvider = new UidDocumentKeyProvider(uidKeyGenerator);

        when(uidKeyGenerator.generate()).thenReturn("");

        uidDocumentKeyProvider.getKey(null);

        verify(uidKeyGenerator).generate();
    }
}

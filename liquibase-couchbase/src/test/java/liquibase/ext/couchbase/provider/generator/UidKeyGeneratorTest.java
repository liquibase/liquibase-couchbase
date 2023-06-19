package liquibase.ext.couchbase.provider.generator;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class UidKeyGeneratorTest {

    private final UidKeyGenerator uidKeyGenerator = new UidKeyGenerator();

    @Test
    void Should_generate_uuid() {
        String uuid = uidKeyGenerator.generate();
        assertThat(uuid).isNotNull();
        assertThat(uuid).hasSize(36);
    }
}

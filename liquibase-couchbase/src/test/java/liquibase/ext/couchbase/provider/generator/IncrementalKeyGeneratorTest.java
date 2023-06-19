package liquibase.ext.couchbase.provider.generator;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class IncrementalKeyGeneratorTest {

    @Test
    void Should_generate_incremental_key() {
        String expectedResult1 = "999";
        String expectedResult2 = "1000";
        AtomicLong holder = new AtomicLong(Long.parseLong(expectedResult1));

        IncrementalKeyGenerator incrementalKeyGenerator = new IncrementalKeyGenerator(holder);

        String key1 = incrementalKeyGenerator.generate();
        String key2 = incrementalKeyGenerator.generate();

        assertThat(key1).isEqualTo(expectedResult1);
        assertThat(key2).isEqualTo(expectedResult2);
    }
}

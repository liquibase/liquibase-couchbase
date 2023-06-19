package liquibase.ext.couchbase.types;

import org.junit.jupiter.api.Test;

import static com.couchbase.client.core.io.CollectionIdentifier.DEFAULT_COLLECTION;
import static com.couchbase.client.core.io.CollectionIdentifier.DEFAULT_SCOPE;
import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static org.assertj.core.api.Assertions.assertThat;

class KeyspaceTest {

    private static final String BUCKET_NAME = "bucket";

    @Test
    void Should_build_default_keyspace() {
        Keyspace defaultKeyspace = Keyspace.defaultKeyspace(BUCKET_NAME);

        assertThat(defaultKeyspace.getBucket()).isEqualTo(BUCKET_NAME);
        assertThat(defaultKeyspace.getCollection()).isEqualTo(DEFAULT_COLLECTION);
        assertThat(defaultKeyspace.getScope()).isEqualTo(DEFAULT_SCOPE);
    }

    @Test
    void Should_return_expected_serialized_object_name() {
        Keyspace defaultKeyspace = Keyspace.defaultKeyspace(BUCKET_NAME);

        assertThat(defaultKeyspace.getSerializedObjectName()).isEqualTo("keyspace");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        Keyspace defaultKeyspace = Keyspace.defaultKeyspace(BUCKET_NAME);

        assertThat(defaultKeyspace.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }
}

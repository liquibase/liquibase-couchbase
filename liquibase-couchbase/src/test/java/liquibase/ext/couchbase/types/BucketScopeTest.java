package liquibase.ext.couchbase.types;

import org.junit.jupiter.api.Test;

import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static org.assertj.core.api.Assertions.assertThat;

class BucketScopeTest {

    private final BucketScope bucketScope = BucketScope.bucketScope("bucket", "scope");

    @Test
    void Should_return_expected_serialized_object_name() {
        assertThat(bucketScope.getSerializedObjectName()).isEqualTo("bucketScope");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        assertThat(bucketScope.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }

}

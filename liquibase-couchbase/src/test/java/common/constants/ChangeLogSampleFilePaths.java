package common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeLogSampleFilePaths {
    private static final String rootPrefix = "liquibase/ext/couchbase";

    public static final String CREATE_COLLECTION_TEST_XML = rootPrefix + "/collection/changelog.create-collection.test.xml";
    public static final String DROP_EXISTING_COLLECTION_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-existing-collection.test.xml";
    public static final String DROP_COLLECTION_IN_NOT_CREATED_BUCKET_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-collection-in-not-created-bucket.test.xml";
    public static final String DROP_COLLECTION_IN_NOT_CREATED_SCOPE_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-collection-in-not-created-scope.test.xml";
    public static final String CREATE_PRIMARY_QUERY_INDEX_TEST_XML = rootPrefix + "/index/changelog.create-primary-query-index.test.xml";
    public static final String CREATE_QUERY_INDEX_TEST_XML = rootPrefix + "/index/changelog.create-query-index.test.xml";
    public static final String DROP_INDEX_TEST_XML = rootPrefix + "/index/changelog.drop-index.test.xml";
    public static final String INSERT_MANY_TEST_XML = rootPrefix + "/insert/changelog.insert-many.test.xml";
    public static final String INSERT_ONE_BROKEN_TEST_XML = rootPrefix + "/insert/changelog.insert-one-broken.test.xml";
    public static final String INSERT_ONE_2_CHANGESETS_ONE_SUCCESSFULL_TEST_XML = rootPrefix + "/insert/" +
            "changelog.insert-one-2-changesets-with-one-broken.test.xml";
    public static final String UPSERT_MANY_TEST_XML = rootPrefix + "/insert/changelog.upsert-many.test.xml";
    public static final String UPSERT_ONE_TEST_XML = rootPrefix + "/insert/changelog.upsert-one.test.xml";
    public static final String CHANGELOG_TEST_XML = rootPrefix + "/changelog/changelog.changelog-test.xml";
    public static final String CHANGELOG_DUPLICATE_TEST_XML = rootPrefix + "/changelog/changelog.changelog-duplicate-test.xml";
    public static final String CREATE_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.create-bucket.test.xml";
    public static final String MUTATE_IN_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in.test.xml";
    public static final String MUTATE_IN_ARRAY_CREATE_TEST_XML = rootPrefix + "/mutatein/changelog.create-array.test.xml";
    public static final String MUTATE_IN_ARRAY_APPEND_TEST_XML = rootPrefix + "/mutatein/changelog.array-append-value.test.xml";
    public static final String MUTATE_IN_ARRAY_PREPEND_TEST_XML = rootPrefix + "/mutatein/changelog.array-prepend-value.test.xml";
    public static final String MUTATE_IN_ARRAY_UNIQUE_TEST_XML = rootPrefix + "/mutatein/changelog.array-unique-value.test.xml";
    public static final String MUTATE_IN_ARRAY_UNIQUE_ERROR_TEST_XML = rootPrefix + "/mutatein/changelog.array-unique-value-error.test.xml";
    public static final String INSERT_UPSERT_STRESS_TEST_XML = rootPrefix + "/stress/stress-test-10k-insert-5k-upsert.xml";

}

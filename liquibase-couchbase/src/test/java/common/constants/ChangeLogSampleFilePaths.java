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
    public static final String DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-not-created-collection-change.test.xml";
    public static final String SKIP_DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML = rootPrefix + "/collection/" +
            "changelog.skip-drop-not-created-collection-change.test.xml";
    public static final String DROP_COLLECTION_IN_NOT_CREATED_SCOPE_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-collection-in-not-created-scope.test.xml";
    public static final String CREATE_PRIMARY_QUERY_INDEX_TEST_XML = rootPrefix + "/index/changelog.create-primary-query-index.test.xml";
    public static final String CREATE_QUERY_INDEX_TEST_XML = rootPrefix + "/index/changelog.create-query-index.test.xml";
    public static final String DROP_INDEX_TEST_XML = rootPrefix + "/index/changelog.drop-index.test.xml";
    public static final String INSERT_MANY_TEST_XML = rootPrefix + "/insert/changelog.insert-many.test.xml";
    public static final String REMOVE_ONE_TEST_XML = rootPrefix + "/remove/changelog.remove-one.test.xml";
    public static final String REMOVE_MANY_TEST_XML = rootPrefix + "/remove/changelog.remove-many.test.xml";
    public static final String INSERT_FROM_FILE_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file.test.xml";
    public static final String UID_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file-uid-key.test.xml";
    public static final String INCREMENT_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file-increment-key.test.xml";
    public static final String EXPRESSION_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file-expression-key.test.xml";
    public static final String INSERT_ONE_BROKEN_TEST_XML = rootPrefix + "/insert/changelog.insert-one-broken.test.xml";
    public static final String INSERT_ONE_2_CHANGESETS_ONE_SUCCESSFULL_TEST_XML = rootPrefix + "/insert/" +
            "changelog.insert-one-2-changesets-with-one-broken.test.xml";
    public static final String UPSERT_MANY_TEST_XML = rootPrefix + "/insert/changelog.upsert-many.test.xml";
    public static final String UPSERT_ONE_TEST_XML = rootPrefix + "/insert/changelog.upsert-one.test.xml";
    public static final String UPSERT_FROM_FILE_TEST_XML = rootPrefix + "/insert/changelog.upsert-from-file.test.xml";
    public static final String CHANGELOG_TEST_XML = rootPrefix + "/changelog/changelog.changelog-test.xml";
    public static final String CHANGELOG_DUPLICATE_TEST_XML = rootPrefix + "/changelog/changelog.changelog-duplicate-test.xml";
    public static final String CHANGELOG_ROLLBACK_BY_COUNT_TEST_XML = rootPrefix + "/changelog/changelog.rollback-by-count-test.xml";
    public static final String CHANGELOG_ROLLBACK_BY_TAG_TEST_XML = rootPrefix + "/changelog/changelog.rollback-by-tag-test.xml";
    public static final String CHANGELOG_TAG_TEST_XML = rootPrefix + "/changelog/changelog.tag-test.xml";
    public static final String CHANGELOG_CONTEXT_LABEL_COMMENT_XML = rootPrefix + "/changelog/changelog.context-label-comment-test.xml";
    public static final String CREATE_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.create-bucket.test.xml";
    public static final String EXECUTE_QUERY_TEST_XML = rootPrefix + "/bucket/changelog.execute-query.test.xml";
    public static final String MUTATE_IN_INSERT_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-insert.test.xml";
    public static final String MUTATE_IN_ARRAY_CREATE_TEST_XML = rootPrefix + "/mutatein/changelog.create-array.test.xml";
    public static final String MUTATE_IN_ARRAY_APPEND_TEST_XML = rootPrefix + "/mutatein/changelog.array-append-value.test.xml";
    public static final String MUTATE_IN_ARRAY_PREPEND_TEST_XML = rootPrefix + "/mutatein/changelog.array-prepend-value.test.xml";
    public static final String MUTATE_IN_ARRAY_UNIQUE_TEST_XML = rootPrefix + "/mutatein/changelog.array-unique-value.test.xml";
    public static final String MUTATE_IN_ARRAY_UNIQUE_ERROR_TEST_XML = rootPrefix + "/mutatein/changelog.array-unique-value-error.test.xml";
    public static final String MUTATE_IN_INCREMENT_DECREMENT_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-increment-decrement" +
            ".test.xml";
    public static final String MUTATE_IN_INCREMENT_DECREMENT_ERROR_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-increment-error" +
            ".test.xml";
    public static final String MUTATE_IN_UPSERT_REPLACE_REMOVE_TEST_XML = rootPrefix + "/mutatein/changelog" +
            ".mutate-in-upsert-replace-remove.test.xml";
    public static final String MUTATE_IN_REPLACE_DOCUMENT_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-replace-document.test.xml";
    public static final String MUTATE_IN_REMOVE_DOCUMENT_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-remove-document.test.xml";
    public static final String MUTATE_IN_INSERT_NO_PATH_ERROR_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-insert-no-path-error" +
            ".test.xml";
    public static final String MUTATE_IN_CREATE_DOCUMENT_AND_INSERT_FIELD_TEST_XML = rootPrefix + "/mutatein/" +
            "changelog.mutate-in-insert-with-creating-document.test.xml";
    public static final String INSERT_UPSERT_STRESS_TEST_XML = rootPrefix + "/stress/stress-test-10k-insert-5k-upsert.xml";
    public static final String DROP_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.drop-bucket.test.xml";
    public static final String DROP_BUCKET_TEST_JSON = rootPrefix + "/bucket/json/drop-bucket.test.json";
    public static final String CREATE_BUCKET_TEST_JSON = rootPrefix + "/bucket/json/changelog.create-bucket.test.json";
    public static final String CREATE_BUCKET_INVALID_CHANGELOG_TEST_JSON = rootPrefix + "/bucket/json/changelog.create-bucket-invalid-changelog.test.json";
    public static final String UPDATE_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.update-bucket.test.xml";
    public static final String CREATE_COLLECTION_SQL_TEST = rootPrefix + "/collection/changelog.create-collection-sql.test.xml";
    public static final String INSERT_DOCUMENT_SQL_TEST = rootPrefix + "/insert/changelog.insert-document-sql.test.xml";
    public static final String INSERT_DOCUMENT_ROLLBACK_SQL_TEST = rootPrefix + "/insert/changelog.insert-document-rollback-sql.test.xml";
    public static final String UPDATE_BUCKET_TEST_JSON = rootPrefix + "/bucket/json/changelog.update-bucket.test.json";

}

package common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeLogSampleFilePaths {
    private static final String rootPrefix = "liquibase/ext/couchbase";

    public static final String CREATE_COLLECTION_TEST_XML = rootPrefix + "/collection/changelog.create-collection.test.xml";
    public static final String CREATE_COLLECTION_DUPLICATE_IGNORE_TEST_XML = rootPrefix + "/collection/changelog.create-collection-duplicate-ignore.test.xml";
    public static final String CREATE_COLLECTION_DUPLICATE_FAIL_TEST_XML = rootPrefix + "/collection/changelog.create-collection-duplicate-fail.test.xml";
    public static final String DROP_EXISTING_COLLECTION_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-existing-collection.test.xml";
    public static final String DROP_EXISTING_COLLECTION_TEST_YML = rootPrefix + "/collection/yaml/" +
            "changelog.drop-existing-collection.test.yml";
    public static final String DROP_COLLECTION_IN_NOT_CREATED_BUCKET_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-collection-in-not-created-bucket.test.xml";
    public static final String DROP_NOT_CREATED_COLLECTION_PRECONDITION_ERROR_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-not-created-collection-change-precondition.test.xml";
    public static final String DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-not-created-collection-change.test.xml";
    public static final String SKIP_DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML = rootPrefix + "/collection/" +
            "changelog.skip-drop-not-created-collection-change.test.xml";
    public static final String DROP_COLLECTION_IN_NOT_CREATED_SCOPE_TEST_XML = rootPrefix + "/collection/" +
            "changelog.drop-collection-in-not-created-scope.test.xml";
    public static final String DROP_SCOPE_TEST_XML = rootPrefix + "/bucket/changelog.drop-scope.test.xml";
    public static final String DROP_NON_EXISTING_SCOPE_ERROR_TEST_XML = rootPrefix + "/bucket/changelog.drop-non-existing-scope-fail.test.xml";
    public static final String DROP_NON_EXISTING_SCOPE_MARK_RUN_TEST_XML = rootPrefix + "/bucket/changelog.drop-non-existing-scope-mark-run.test.xml";
    public static final String CREATE_PRIMARY_QUERY_INDEX_TEST_XML = rootPrefix + "/index/changelog.create-primary-query-index.test.xml";
    public static final String CREATE_QUERY_INDEX_TEST_XML = rootPrefix + "/index/changelog.create-query-index.test.xml";
    public static final String DROP_INDEX_TEST_XML = rootPrefix + "/index/changelog.drop-index.test.xml";
    public static final String DROP_INDEX_SYSTEM_TEST_XML = rootPrefix + "/index/changelog.drop-index-system.test.xml";
    public static final String DROP_PRIMARY_INDEX_SYSTEM_TEST_XML = rootPrefix + "/index/changelog.drop-primary-index-system.test.xml";
    public static final String DROP_PRIMARY_INDEX_BY_NAME_SYSTEM_TEST_XML = rootPrefix + "/index/changelog.drop-primary-index-by-name-system.test.xml";
    public static final String DROP_INDEX_SYSTEM_TEST_MARK_RUN_XML = rootPrefix + "/index/changelog.drop-index-system-mark-run.test.xml";
    public static final String DROP_NON_EXISTING_INDEX_SYSTEM_TEST_ERROR_XML = rootPrefix + "/index/changelog.drop-non-existing-index-system-error.test.xml";
    public static final String INSERT_MANY_TEST_XML = rootPrefix + "/insert/changelog.insert-many.test.xml";
    public static final String REMOVE_ONE_TEST_XML = rootPrefix + "/remove/changelog.remove-one.test.xml";
    public static final String REMOVE_BY_QUERY_TEST_XML = rootPrefix + "/remove/changelog.remove-by-query.test.xml";
    public static final String REMOVE_ONE_DOCUMENT_TEST_XML = rootPrefix + "/remove/changelog.remove-one-document.test.xml";
    public static final String REMOVE_DOCUMENTS_TEST_XML = rootPrefix + "/remove/changelog.remove-documents.test.xml";
    public static final String REMOVE_NON_EXISTING_DOC_MARK_AS_READ_TEST_XML = rootPrefix + "/remove/changelog.remove-non-existing-doc-mark-as-run.test.xml";
    public static final String REMOVE_NON_EXISTING_DOC_ERROR_TEST_XML = rootPrefix + "/remove/changelog.remove-non-existing-doc-error.test.xml";
    public static final String REMOVE_MANY_TEST_XML = rootPrefix + "/remove/changelog.remove-many.test.xml";
    public static final String INSERT_FROM_FILE_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file.test.xml";
    public static final String INSERT_DOCUMENTS_TEST_XML = rootPrefix + "/insert/changelog.insert-documents.test.xml";
    public static final String INSERT_DOCUMENTS_FAILED_TRANSACTION_TEST_XML = rootPrefix + "/insert/changelog.insert-documents-failed-transaction.test.xml";
    public static final String INSERT_UID_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file-uid-key.test.xml";
    public static final String INSERT_INCREMENT_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file-increment-key.test.xml";
    public static final String INSERT_EXPRESSION_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.insert-from-file-expression-key.test.xml";
    public static final String UPSERT_MANY_TEST_XML = rootPrefix + "/insert/changelog.upsert-many.test.xml";
    public static final String UPSERT_FROM_FILE_TEST_XML = rootPrefix + "/insert/changelog.upsert-from-file.test.xml";
    public static final String UPSERT_UID_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.upsert-from-file-uid-key.test.xml";

    public static final String UPSERT_INCREMENT_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.upsert-from-file-increment-key.test.xml";

    public static final String UPSERT_EXPRESSION_KEY_GENERATOR_TEST_XML = rootPrefix + "/insert/changelog.upsert-from-file-expression-key.test.xml";
    public static final String UPSERT_DOCUMENTS_TEST_XML = rootPrefix + "/insert/changelog.upsert-documents.test.xml";
    public static final String UPSERT_DOCUMENTS_FAILED_TRANSACTION_TEST_XML = rootPrefix + "/insert/changelog.upsert-documents-failed-transaction.test.xml";
    public static final String CHANGELOG_TEST_XML = rootPrefix + "/changelog/changelog.changelog-test.xml";
    public static final String CHANGELOG_DUPLICATE_TEST_XML = rootPrefix + "/changelog/changelog.changelog-duplicate-test.xml";
    public static final String CHANGELOG_ROLLBACK_BY_COUNT_TEST_XML = rootPrefix + "/changelog/changelog.rollback-by-count-test.xml";
    public static final String CHANGELOG_ROLLBACK_BY_TAG_TEST_XML = rootPrefix + "/changelog/changelog.rollback-by-tag-test.xml";
    public static final String CHANGELOG_TAG_TEST_XML = rootPrefix + "/changelog/changelog.tag-test.xml";
    public static final String CHANGELOG_CONTEXT_LABEL_COMMENT_XML = rootPrefix + "/changelog/changelog.context-label-comment-test.xml";
    public static final String CREATE_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.create-bucket.test.xml";
    public static final String CREATE_DUPLICATE_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.create-duplicate-bucket.test.xml";
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
    public static final String MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENT_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-replace-document-query-filter.test.xml";
    public static final String MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENTS_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-replace-documents-query-filter.test.xml";
    public static final String MUTATE_IN_REMOVE_DOCUMENT_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-in-remove-document.test.xml";
    public static final String MUTATE_IN_INSERT_NO_PATH_ERROR_TEST_XML = rootPrefix + "/mutatein/changelog.mutate-insert-no-path-error" +
            ".test.xml";
    public static final String MUTATE_IN_CREATE_DOCUMENT_AND_INSERT_FIELD_TEST_XML = rootPrefix + "/mutatein/" +
            "changelog.mutate-in-insert-with-creating-document.test.xml";
    public static final String INSERT_UPSERT_STRESS_TEST_XML = rootPrefix + "/stress/stress-test-10k-insert-5k-upsert.xml";
    public static final String DROP_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.drop-bucket.test.xml";
    public static final String DROP_BUCKET_MARK_RUN_TEST_XML = rootPrefix + "/bucket/changelog.drop-bucket-mark-run.test.xml";
    public static final String DROP_BUCKET_TEST_YML = rootPrefix + "/bucket/yaml/changelog.drop-bucket.test.yml";
    public static final String DROP_BUCKET_TEST_JSON = rootPrefix + "/bucket/json/drop-bucket.test.json";
    public static final String CREATE_BUCKET_TEST_JSON = rootPrefix + "/bucket/json/changelog.create-bucket.test.json";
    public static final String CREATE_BUCKET_TEST_YAML = rootPrefix + "/bucket/yaml/changelog.create-bucket.test.yml";
    public static final String CREATE_BUCKET_INVALID_CHANGELOG_TEST_JSON = rootPrefix + "/bucket/json/changelog.create-bucket-invalid-changelog.test.json";
    public static final String UPDATE_BUCKET_TEST_XML = rootPrefix + "/bucket/changelog.update-bucket.test.xml";
    public static final String CREATE_COLLECTION_SQL_TEST = rootPrefix + "/collection/changelog.create-collection-sql.test.xml";
    public static final String INSERT_DOCUMENT_SQL_TEST = rootPrefix + "/insert/changelog.insert-document-sql.test.xml";
    public static final String INSERT_DOCUMENT_ROLLBACK_SQL_TEST = rootPrefix + "/insert/changelog.insert-document-rollback-sql.test.xml";
    public static final String UPDATE_BUCKET_TEST_JSON = rootPrefix + "/bucket/json/changelog.update-bucket.test.json";
    public static final String CREATE_SCOPE_TEST = rootPrefix + "/scope/changelog.create-scope.test.xml";

    //preconditions
    public static final String BUCKET_EXISTS_PRECONDITION = rootPrefix + "/precondition/changelog.bucket-exists-precondition.test.xml";
    public static final String BUCKET_EXISTS_FAILED_PRECONDITION = rootPrefix + "/precondition/changelog.bucket-exists-precondition-failed.test.xml";
    public static final String SCOPE_EXISTS_PRECONDITION = rootPrefix + "/precondition/changelog.scope-exists-precondition.test.xml";
    public static final String SCOPE_EXISTS_FAILED_PRECONDITION = rootPrefix + "/precondition/changelog.scope-exists-precondition-failed.test.xml";
    public static final String COLLECTION_EXISTS_PRECONDITION = rootPrefix + "/precondition/changelog.collection-exists-precondition.test.xml";
    public static final String COLLECTION_EXISTS_FAILED_PRECONDITION = rootPrefix + "/precondition/changelog.collection-exists-precondition-failed.test.xml";
    public static final String DOCUMENT_EXISTS_PRECONDITION = rootPrefix + "/precondition/changelog.document-exists-precondition.test.xml";
    public static final String DOCUMENT_EXISTS_FAILED_PRECONDITION = rootPrefix + "/precondition/changelog.document-exists-precondition-failed.test.xml";
    public static final String INDEX_EXISTS_PRECONDITION = rootPrefix + "/precondition/changelog.index-exists-precondition.test.xml";
    public static final String INDEX_EXISTS_FAILED_PRECONDITION = rootPrefix + "/precondition/changelog.index-exists-precondition-failed.test.xml";
    public static final String PRIMARY_INDEX_EXISTS_PRECONDITION = rootPrefix + "/precondition/changelog.primary-index-exists-precondition.test.xml";
    public static final String PRIMARY_INDEX_EXISTS_FAILED_PRECONDITION = rootPrefix + "/precondition/changelog.primary-index-exists-precondition-failed.test.xml";
    public static final String SQL_CHECK_PRECONDITION = rootPrefix + "/precondition/changelog.sql-check-precondition.test.xml";
    public static final String SQL_CHECK_FAILED_PRECONDITION = rootPrefix + "/precondition/changelog.sql-check-precondition-failed.test.xml";

}

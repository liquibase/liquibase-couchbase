package common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeLogSampleFilePaths {
    private static final String prefix = "liquibase/ext/couchbase/changelog.";

    public static final String CREATE_COLLECTION_TEST_XML = prefix + "create-collection.test.xml";
    public static final String CREATE_PRIMARY_QUERY_INDEX_TEST_XML = prefix + "create-primary-query-index.test.xml";
    public static final String CREATE_QUERY_INDEX_TEST_XML = prefix + "create-query-index.test.xml";
    public static final String DROP_INDEX_TEST_XML = prefix + "drop-index.test.xml";
    public static final String INSERT_MANY_TEST_XML = prefix + "insert-many.test.xml";
    public static final String UPSERT_MANY_TEST_XML = prefix + "upsert-many.test.xml";
    public static final String UPSERT_ONE_TEST_XML = prefix + "upsert-one.test.xml";

    public static final String CHANGELOG_TEST_XML = "liquibase/ext/couchbase/changelog/changelog.changelog-test.xml";
    public static final String CHANGELOG_DUPLICATE_TEST_XML = "liquibase/ext/couchbase/changelog/changelog.changelog-duplicate-test.xml";

}

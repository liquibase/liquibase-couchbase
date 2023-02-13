package common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeLogSampleFilePaths {

    public static final String CREATE_COLLECTION_TEST_XML = "liquibase/ext/couchbase/changelog.create-collection.test.xml";
    public static final String CREATE_PRIMARY_QUERY_INDEX_TEST_XML = "liquibase/ext/couchbase/changelog.create-primary-query-index.test.xml";
    public static final String CREATE_QUERY_INDEX_TEST_XML = "liquibase/ext/couchbase/changelog.create-query-index.test.xml";
    public static final String DROP_INDEX_TEST_XML = "liquibase/ext/couchbase/changelog.drop-index.test.xml";
    public static final String INSERT_MANY_TEST_XML = "liquibase/ext/couchbase/changelog.insert-many.test.xml";
    public static final String UPSERT_MANY_TEST_XML = "liquibase/ext/couchbase/changelog.upsert-many.test.xml";
    public static final String CHANGELOG_TEST_XML = "liquibase/ext/couchbase/changelog/changelog.changelog-test.xml";
    public static final String CHANGELOG_DUPLICATE_TEST_XML = "liquibase/ext/couchbase/changelog/changelog.changelog-duplicate-test.xml";

}

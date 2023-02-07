package liquibase.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeLogSampleFilePaths {

    public static final String CREATE_COLLECTION_TEST_XML = "liquibase.ext/changelog.create-collection.test.xml";
    public static final String CREATE_PRIMARY_QUERY_INDEX_TEST_XML = "liquibase.ext/changelog.create-primary-query-index.test.xml";
    public static final String CREATE_QUERY_INDEX_TEST_XML = "liquibase.ext/changelog.create-query-index.test.xml";

}

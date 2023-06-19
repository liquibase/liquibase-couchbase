package liquibase.ext.couchbase.database;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final int DEFAULT_PORT = 8091;
    public static final int COUCHBASE_PRIORITY = 500;

    public static final String COUCHBASE_PRODUCT_NAME = "Couchbase";
    public static final String COUCHBASE_PRODUCT_SHORT_NAME = "couchbase";
    public static final String COUCHBASE_PREFIX = COUCHBASE_PRODUCT_SHORT_NAME + "://";
    public static final String COUCHBASE_SSL_PREFIX = COUCHBASE_PRODUCT_SHORT_NAME + "s://";
    public static final String BUCKET_PARAM = "bucket";
    public static final String COUCHBASE_EXTENSION_JSON_SCHEMA = "www.liquibase.org/xml/ns/dbchangelog/dbchangelog-couchbase-ext.json";

}

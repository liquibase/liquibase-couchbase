package common

import liquibase.ext.couchbase.types.Keyspace

class HarnessTestConstants {
    public static final def HARNESS_BUCKET = "harnessBucket"
    public static final def HARNESS_SCOPE = "harnessScope"
    public static final def HARNESS_COLLECTION = "harnessCollection"
    public static final def keyspace = Keyspace.keyspace(HARNESS_BUCKET, HARNESS_SCOPE, HARNESS_COLLECTION)

    public static final String EXPECTED_FOLDER = "liquibase/harness/compatibility/foundational/expectedResultSet"
    public static final String CHANGELOGS_FOLDER = "liquibase/harness/compatibility/foundational/changelogs/couchbase"
}

package liquibase.ext.couchbase.lockservice;

public enum LockStatus {

    FREE,
    OWNER,
    NOT_OWNER
}

package liquibase.ext.couchbase.lockservice;

public enum LockStatus {
    LOCKED_BY_SERVICE,
    LOCKED_BY_ANOTHER_SERVICE,
    NO_LOCK

}

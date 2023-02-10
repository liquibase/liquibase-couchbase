package liquibase.ext.couchbase.changelog;

import liquibase.changelog.ChangeSet;
import liquibase.database.Database;

import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

public class CouchbaseHistoryService extends NoSqlHistoryService {
    public CouchbaseHistoryService() {
        //TODO
        super();
    }

    @Override
    public int getPriority() {
        return PRIORITY_SPECIALIZED;
    }

    @Override
    public boolean supports(final Database database) {
        //TODO
        return true;
    }

    public String extractTag(final ChangeSet changeSet) {
        //TODO implement
        return "";
    }
}

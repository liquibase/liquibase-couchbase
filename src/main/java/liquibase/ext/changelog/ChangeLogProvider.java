package liquibase.ext.changelog;

import liquibase.changelog.DatabaseChangeLog;

public interface ChangeLogProvider {

    DatabaseChangeLog load(String path);

}

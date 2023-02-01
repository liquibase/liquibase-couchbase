package liquibase.ext.change;

import liquibase.ext.changelog.ChangeLogProvider;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class TestChangeLogProvider implements ChangeLogProvider {

    private final CouchbaseLiquibaseDatabase database;

    @Override
    @SneakyThrows
    public DatabaseChangeLog load(String path) {
        final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        final ChangeLogParser parser = ChangeLogParserFactory.getInstance().getParser(
                path, resourceAccessor
        );

        return parser.parse(path, new ChangeLogParameters(database), resourceAccessor);
    }
}

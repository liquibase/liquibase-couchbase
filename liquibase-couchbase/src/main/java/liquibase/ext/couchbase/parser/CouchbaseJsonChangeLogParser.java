package liquibase.ext.couchbase.parser;

import liquibase.Scope;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.ext.couchbase.validator.JsonChangelogValidator;
import liquibase.logging.Logger;
import liquibase.parser.core.json.JsonChangeLogParser;
import liquibase.resource.ResourceAccessor;

import static java.lang.String.format;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_EXTENSION_JSON_SCHEMA;

public class CouchbaseJsonChangeLogParser extends JsonChangeLogParser {

    private final Logger log = Scope.getCurrentScope().getLog(getClass());

    private final JsonChangelogValidator jsonChangelogValidator = Scope.getCurrentScope().getSingleton(JsonChangelogValidator.class);

    @Override
    public DatabaseChangeLog parse(String path,
                                   ChangeLogParameters params,
                                   ResourceAccessor resourceAccessor) throws ChangeLogParseException {
        log.info(format("Starting to parse [%s] changelog file", path));
        jsonChangelogValidator.validateChangeLogFile(path, COUCHBASE_EXTENSION_JSON_SCHEMA);
        return super.parse(path, params, resourceAccessor);
    }

}

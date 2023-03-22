package liquibase.ext.couchbase.change;

import liquibase.Scope;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.reader.N1qlFileReader;
import liquibase.ext.couchbase.statement.N1qlStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Part of change set package. Responsible for executing n1ql(sql++) queries from .sql file
 *
 * @see N1qlStatement
 */
@DatabaseChange(name = "n1ql",
                description = "Executes sql++ couchbase query " + "https://docs.couchbase.com/server/current/getting-started/try-a-query" +
                        ".html",
                priority = PrioritizedService.PRIORITY_DATABASE,
                appliesTo = {"database"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class N1qlChange extends CouchbaseChange {

    private final N1qlFileReader n1QlFileReader = Scope.getCurrentScope().getSingleton(N1qlFileReader.class);

    private String filePath;

    private Boolean transactional;

    @Override
    public String getConfirmationMessage() {
        return String.format("The queries located in %s file has been executed successfully", filePath);
    }

    @Override
    public SqlStatement[] generateStatements() {
        String changeLog = n1QlFileReader.load(filePath);

        List<String> queries = n1QlFileReader.retrieveQueriesFromSqlChangelog(changeLog);

        return new SqlStatement[] {new N1qlStatement(queries, transactional)};
    }

}
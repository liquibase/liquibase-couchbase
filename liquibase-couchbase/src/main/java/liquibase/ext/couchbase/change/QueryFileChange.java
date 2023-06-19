package liquibase.ext.couchbase.change;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.CouchbaseSqlStatement;
import liquibase.ext.couchbase.types.File;
import liquibase.resource.Resource;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Part of change set package. Responsible for executing n1ql(sql++) queries from .sql file
 * @see CouchbaseSqlStatement
 */
@DatabaseChange(name = "executeQueryFile",
                description = "Executes sql++ couchbase query " + "https://docs.couchbase.com/server/current/getting-started/try-a-query" +
                        ".html",
                priority = ChangeMetaData.PRIORITY_DEFAULT + 1,
                appliesTo = {"database"})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryFileChange extends CouchbaseChange {

    private String path;
    private Boolean transactional;
    private File file;

    @Override
    public String getConfirmationMessage() {
        return String.format("The queries located in %s file has been executed successfully", path);
    }

    @Override
    public SqlStatement[] generateStatements() {
        Resource resource = evaluateResource();
        return new SqlStatement[] {new CouchbaseSqlStatement(resource, transactional)};
    }

    @SneakyThrows
    private Resource evaluateResource() {
        return file.getAsResource(getChangeSet().getFilePath());
    }

}
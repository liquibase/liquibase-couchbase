package liquibase.ext.couchbase.change;

import com.couchbase.client.java.kv.MutateInOptions;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.kv.StoreSemantics;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.MutateInQueryStatement;
import liquibase.ext.couchbase.statement.MutateInSqlQueryStatement;
import liquibase.ext.couchbase.statement.MutateInStatement;
import liquibase.ext.couchbase.transformer.MutateInSpecTransformer;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.couchbase.client.java.kv.MutateInOptions.mutateInOptions;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.MUTATE_IN_TIMEOUT;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 * Part of change set package. Responsible for executing mutateIn operation by filtering data via id or sql++ query(sqlPlusPlusQuery or whereCondition field).
 * In 'whereCondition' field only condition need to be provided, e.g. fieldName="test"<br><br>
 * In 'sqlPlusPlusQuery' the full query need to be provided<br><br>
 * @link <a href="https://docs.couchbase.com/java-sdk/current/howtos/subdocument-operations.html">Reference documentation</a>
 * @see MutateInQueryStatement
 * @see MutateInStatement
 * @see LiquibaseMutateInSpec
 */
@Data
@DatabaseChange(
        name = "mutateIn",
        description = "https://docs.couchbase.com/java-sdk/current/howtos/subdocument-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class MutateInChange extends CouchbaseChange {

    private String id;
    private String whereCondition;
    private String sqlPlusPlusQuery;
    private String bucketName;
    private String scopeName;
    private String collectionName;
    private String expiry;
    private Boolean preserveExpiry;
    private StoreSemantics storeSemantics;
    private List<LiquibaseMutateInSpec> mutateInSpecs = new ArrayList<>();
    private static final MutateInSpecTransformer mutateInSpecTransformer = new MutateInSpecTransformer();

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        MutateIn mutate = buildMutate(keyspace);
        MutateInOptions mutateInOptions = buildOptions(expiry, preserveExpiry, storeSemantics);

        if (id == null && whereCondition == null) {
            return new SqlStatement[] {
                    new MutateInSqlQueryStatement(mutate, mutateInOptions, sqlPlusPlusQuery)
            };
        }
        if (id == null) {
            return new SqlStatement[] {
                    new MutateInQueryStatement(mutate, mutateInOptions, whereCondition)
            };
        }
        return new SqlStatement[] {
                new MutateInStatement(mutate, mutateInOptions)
        };
    }

    @Override
    public String getConfirmationMessage() {
        int opCount = mutateInSpecs.size();
        return format("MutateIn %s operations has been successfully executed", opCount);
    }

    private MutateInOptions buildOptions(String expiry, Boolean preserveExpiry, StoreSemantics storeSemantics) {
        MutateInOptions options = mutateInOptions();
        options.timeout(MUTATE_IN_TIMEOUT.getCurrentValue());
        ofNullable(expiry)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(value -> options.expiry(Duration.parse(value)));
        ofNullable(preserveExpiry)
                .ifPresent(options::preserveExpiry);
        ofNullable(storeSemantics)
                .ifPresent(options::storeSemantics);
        return options;
    }

    private MutateIn buildMutate(Keyspace keyspace) {
        List<MutateInSpec> specs = mutateInSpecs.stream()
                .map(mutateInSpecTransformer::toSpec)
                .collect(toList());
        return MutateIn.builder()
                .id(id)
                .keyspace(keyspace)
                .specs(specs)
                .build();
    }

}

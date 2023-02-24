package liquibase.ext.couchbase.change;

import com.couchbase.client.java.kv.MutateInSpec;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.transformer.MutateInSpecTransformer;
import liquibase.ext.couchbase.statement.MutateInStatement;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

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

    private String bucketName;
    private String scopeName;
    private String collectionName;

    private List<LiquibaseMutateInSpec> mutateInSpecs = new ArrayList<>();

    private static final MutateInSpecTransformer mutateInSpecTransformer = new MutateInSpecTransformer();

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        MutateIn mutate = buildMutate(keyspace);
        return new SqlStatement[] {
                new MutateInStatement(mutate)
        };
    }

    @Override
    public String getConfirmationMessage() {
        int opCount = mutateInSpecs.size();
        return format("MutateIn %s operations has been successfully fulfilled", opCount);
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

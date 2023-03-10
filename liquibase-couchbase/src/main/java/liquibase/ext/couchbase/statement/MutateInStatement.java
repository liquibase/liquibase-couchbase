package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutateInOptions;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.couchbase.client.java.kv.MutateInOptions.mutateInOptions;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.MUTATE_IN_TIMEOUT;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MutateInStatement extends CouchbaseStatement {

    private static final MutateInOptions mutateInOptions = mutateInOptions().timeout(MUTATE_IN_TIMEOUT.getCurrentValue());
    private final MutateIn mutate;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        Keyspace keyspace = mutate.getKeyspace();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());
        Collection collection = bucketOperator.getCollection(keyspace.getCollection(), keyspace.getScope());

        collection.mutateIn(mutate.getId(), mutate.getSpecs(), mutateInOptions);
    }
}


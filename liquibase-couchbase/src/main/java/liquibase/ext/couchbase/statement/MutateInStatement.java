package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MutateInStatement extends CouchbaseStatement {

    private final MutateIn mutate;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        Keyspace keyspace = mutate.getKeyspace();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());
        Collection collection = bucketOperator.getCollection(keyspace.getCollection(), keyspace.getScope());

        collection.mutateIn(mutate.getId(), mutate.getSpecs());
    }
}


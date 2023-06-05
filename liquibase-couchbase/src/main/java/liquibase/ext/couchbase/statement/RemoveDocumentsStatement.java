package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import java.util.Set;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RemoveDocumentsStatement extends CouchbaseTransactionStatement {

    private final Keyspace keyspace;
    private final Set<Id> ids;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope())
                .removeDocsTransactionally(transaction, ids);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        return clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope())
                .removeDocsTransactionallyReactive(transaction, ids);
    }

}

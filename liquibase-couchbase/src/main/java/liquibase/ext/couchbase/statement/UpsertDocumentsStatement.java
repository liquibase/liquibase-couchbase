package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import java.util.List;

/**
 * A statement to upsert many instances of a {@link Document} inside one transaction into a keyspace
 * @see Document
 * @see CouchbaseStatement
 * @see Keyspace
 */

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertDocumentsStatement extends CouchbaseTransactionStatement {

    private final Keyspace keyspace;
    private final List<Document> documents;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope())
                .upsertDocsTransactionally(transaction, documents);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                ClusterOperator clusterOperator) {
        return clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope())
                .upsertDocsTransactionallyReactive(transaction, documents);
    }
}


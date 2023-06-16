package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.ImportFile;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

/**
 * A statement to upsert documents from file inside one transaction into a keyspace
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html"/>
 * @see Document
 * @see CouchbaseStatement
 * @see Keyspace
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertFileContentStatement extends CouchbaseFileContentStatement {
    private final Keyspace keyspace;
    private final ImportFile file;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        getCollectionOperator(clusterOperator)
                .upsertDocsTransactionally(transaction, getDocsFromFile(file));
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        return getCollectionOperator(clusterOperator)
                .upsertDocsTransactionallyReactive(transaction, getDocsFromFile(file));
    }

    private CollectionOperator getCollectionOperator(ClusterOperator clusterOperator) {
        return clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope());
    }
}


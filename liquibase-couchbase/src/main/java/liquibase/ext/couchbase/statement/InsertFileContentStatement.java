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

import java.util.List;

/**
 * A statement to insert documents from file inside one transaction into a keyspace
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html"/>
 * @see Document
 * @see CouchbaseStatement
 * @see Keyspace
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsertFileContentStatement extends CouchbaseFileContentStatement {
    private final Keyspace keyspace;
    private final ImportFile importFile;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        List<Document> docs = getDocsFromFile(importFile);
        CollectionOperator collectionOperator = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope());
        collectionOperator.insertDocsTransactionally(transaction, docs);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        List<Document> docs = getDocsFromFile(importFile);
        CollectionOperator collectionOperator = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope());
        return collectionOperator.insertDocsTransactionallyReactive(transaction, docs);
    }
}


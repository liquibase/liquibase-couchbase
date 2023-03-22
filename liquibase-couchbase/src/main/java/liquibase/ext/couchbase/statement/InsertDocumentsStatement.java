package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;

/**
 * A statement to insert many instances of a {@link Document} inside one transaction into a keyspace
 * @see Document
 * @see CouchbaseStatement
 * @see Keyspace
 */

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsertDocumentsStatement extends CouchbaseTransactionStatement {

    private final Keyspace keyspace;
    private final List<Document> documents;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        Map<String, Object> contentList = clusterOperator.checkDocsAndTransformToObjects(documents);
        clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope())
                .insertDocsTransactionally(transaction, contentList);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                ClusterOperator clusterOperator) {
        Map<String, Object> contentList = clusterOperator.checkDocsAndTransformToObjects(documents);
        CollectionOperator collectionOperator = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope());

        return collectionOperator.insertDocsTransactionallyReactive(transaction, contentList);
    }

}


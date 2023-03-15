package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * A statement to insert documents from file inside one transaction into a keyspace
 *
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
    private final File file;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        CollectionOperator collectionOperator = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope());
        Map<String, Object> docs = getDocsFromFile(file, clusterOperator);
        collectionOperator.upsertDocsTransactionally(transaction, docs);
    }
}


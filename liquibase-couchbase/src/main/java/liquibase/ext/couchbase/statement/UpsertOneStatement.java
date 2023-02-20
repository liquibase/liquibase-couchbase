package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;

import liquibase.ext.couchbase.types.Document;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static java.util.Collections.singletonList;

/**
 *
 * A statement to upsert one {@link Document} inside one transaction into a keyspace
 *
 * @see Document
 * @see CouchbaseStatement
 * @see Keyspace
 *
 */

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertOneStatement extends CouchbaseTransactionStatement {

    private final Keyspace keyspace;
    private final Document document;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction,
                            ClusterOperator clusterOperator) {
        UpsertManyStatement statement = new UpsertManyStatement(keyspace, singletonList(document));
        statement.doInTransaction(transaction, clusterOperator);
    }
}


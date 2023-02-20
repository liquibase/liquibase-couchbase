package common;

import common.operators.TestClusterOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.CouchbaseTransactionAction;

/**
 * Test class for statements (DML) to execute it inside transaction.
 */
public class TransactionStatementTest extends BucketTestCase {

    protected static ClusterOperator clusterOperator = new TestClusterOperator(cluster);

    /**
     * Executes statements in transaction
     */
    @SafeVarargs
    protected final void doInTransaction(CouchbaseTransactionAction... statements){
        cluster.transactions().run(transaction -> {
            for (CouchbaseTransactionAction statement : statements) {
                statement.accept(transaction);
            }
        });
    }

    /**
     * Method for imitating failed transaction
     */
    @SafeVarargs
    protected final void doInFailingTransaction(CouchbaseTransactionAction... statements){
        cluster.transactions().run(transaction -> {
            for (CouchbaseTransactionAction statement : statements) {
                statement.accept(transaction);
            }
            throw new RuntimeException("Some exception during transaction execution");
        });
    }
}

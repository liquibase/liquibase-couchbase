package liquibase.ext.couchbase.exception;

import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;

import static java.lang.String.format;

public class TransactionalReactiveStatementExecutionException extends RuntimeException {

    private static final String withClassMsg = "An error was occurred in transactional reactive statement %s execution";
    private static final String msg = "An error was occurred in transactional reactive statement execution ";

    public TransactionalReactiveStatementExecutionException(Class<? extends CouchbaseTransactionStatement> clz, TransactionFailedException e) {
        super(format(withClassMsg, clz.getName()), e);
    }

    public TransactionalReactiveStatementExecutionException(TransactionFailedException e) {
        super(msg + e.logs(), e);
    }

}

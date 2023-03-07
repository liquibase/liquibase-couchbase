package liquibase.ext.couchbase.exception;

import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;

import static java.lang.String.format;

public class TransactionalStatementExecutionException extends RuntimeException {

    private static final String withClassMsg = "An error was occured in transactional statement %s execution";
    private static final String msg = "An error was occured in transactional statement execution ";

    public TransactionalStatementExecutionException(Class<? extends CouchbaseTransactionStatement> clz, TransactionFailedException e) {
        super(format(withClassMsg, clz.getName()), e);
    }

    public TransactionalStatementExecutionException(TransactionFailedException e) {
        super(msg + e.logs(), e);
    }

}

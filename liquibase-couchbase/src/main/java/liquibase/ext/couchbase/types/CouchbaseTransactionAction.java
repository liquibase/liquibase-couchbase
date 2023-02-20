package liquibase.ext.couchbase.types;

import com.couchbase.client.java.transactions.TransactionAttemptContext;

import java.util.function.Consumer;

public interface CouchbaseTransactionAction extends Consumer<TransactionAttemptContext> {
}

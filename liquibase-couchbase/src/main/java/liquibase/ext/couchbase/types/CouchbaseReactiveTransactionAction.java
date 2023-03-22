package liquibase.ext.couchbase.types;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import org.reactivestreams.Publisher;

import java.util.function.Function;

public interface CouchbaseReactiveTransactionAction extends Function<ReactiveTransactionAttemptContext, Publisher<?>> {
}

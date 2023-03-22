package liquibase.ext.couchbase.executor;

import liquibase.SingletonObject;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.types.CouchbaseReactiveTransactionAction;

import java.util.LinkedList;

/**
 * Stores statements in a queue for executing them in one transaction.<br >
 * @see  CouchbaseConnection
 */
public class TransactionalReactiveStatementQueue extends LinkedList<CouchbaseReactiveTransactionAction> implements SingletonObject {


}
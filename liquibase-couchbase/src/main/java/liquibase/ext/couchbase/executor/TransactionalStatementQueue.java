package liquibase.ext.couchbase.executor;

import java.util.LinkedList;

import liquibase.SingletonObject;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.types.CouchbaseTransactionAction;

/**
 * Stores statements in a queue for executing them in one transaction.<br >
 * @see  CouchbaseConnection
 */
public class TransactionalStatementQueue extends LinkedList<CouchbaseTransactionAction> implements SingletonObject {


}
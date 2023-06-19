package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.collections4.SetUtils;
import org.reactivestreams.Publisher;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RemoveDocumentsSqlQueryStatement extends RemoveDocumentsStatement {

    private final String sqlPlusPlusQuery;

    public RemoveDocumentsSqlQueryStatement(Keyspace keyspace, Set<Id> ids, String sqlPlusPlusQuery) {
        super(keyspace, ids);
        this.sqlPlusPlusQuery = sqlPlusPlusQuery;
    }

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        initSelectIds(clusterOperator);
        super.doInTransaction(transaction, clusterOperator);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        initSelectIds(clusterOperator);
        return super.doInTransactionReactive(transaction, clusterOperator);
    }

    private void initSelectIds(ClusterOperator clusterOperator) {
        Set<Id> filteredIds = clusterOperator.retrieveDocumentIdsBySqlPlusPlusQuery(sqlPlusPlusQuery)
                .stream().map(Id::new).collect(toSet());
        setIds(SetUtils.union(filteredIds, getIds()).toSet());
    }

}

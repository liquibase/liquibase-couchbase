package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RemoveDocumentsQueryStatement extends RemoveDocumentsStatement {

    private final String whereCondition;

    public RemoveDocumentsQueryStatement(Keyspace keyspace, Set<Id> ids, String whereCondition) {
        super(keyspace, ids);
        this.whereCondition = whereCondition;
    }

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        List<String> filteredIds = clusterOperator.retrieveDocumentIdsByWhereClause(getKeyspace(), whereCondition);
        getIds().addAll(filteredIds.stream().map(Id::new).collect(Collectors.toList()));
        super.doInTransaction(transaction, clusterOperator);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        List<String> filteredIds = clusterOperator.retrieveDocumentIdsByWhereClause(getKeyspace(), whereCondition);
        getIds().addAll(filteredIds.stream().map(Id::new).collect(Collectors.toList()));
        return super.doInTransactionReactive(transaction, clusterOperator);
    }

}

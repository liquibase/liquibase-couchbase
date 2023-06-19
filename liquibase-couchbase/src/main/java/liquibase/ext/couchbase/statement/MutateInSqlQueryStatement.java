package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutateInOptions;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * A statement to perform mutateIn by filtering ids of documents via sql++ query.
 *
 * @see MutateIn
 * @see MutateInOptions
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MutateInSqlQueryStatement extends CouchbaseStatement {

    private final MutateIn mutate;
    private final MutateInOptions mutateInOptions;
    private final String sqlPlusPlusQuery;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        Keyspace keyspace = mutate.getKeyspace();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());
        Collection collection = bucketOperator.getCollection(keyspace.getCollection(), keyspace.getScope());
        clusterOperator.retrieveDocumentIdsBySqlPlusPlusQuery(sqlPlusPlusQuery)
                .forEach(documentId -> collection.mutateIn(documentId, mutate.getSpecs(), mutateInOptions));
    }

}

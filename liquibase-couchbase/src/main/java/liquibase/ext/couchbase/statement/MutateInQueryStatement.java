package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutateInOptions;
import com.couchbase.client.java.query.QueryResult;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MutateInQueryStatement extends CouchbaseStatement {

    private static final String RETRIEVE_DOCUMENT_IDS_QUERY_TEMPLATE = "SELECT meta().id FROM %s WHERE %s";
    private static final String COLLECTION_PATH_TEMPLATE = "`%s`.`%s`.`%s`";
    private final MutateIn mutate;
    private final MutateInOptions mutateInOptions;
    private final String whereClause;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        Keyspace keyspace = mutate.getKeyspace();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());
        Collection collection = bucketOperator.getCollection(keyspace.getCollection(), keyspace.getScope());
        retrieveDocumentIds(keyspace, clusterOperator)
                .forEach(documentId -> collection.mutateIn(documentId, mutate.getSpecs(), mutateInOptions));
    }

    private List<String> retrieveDocumentIds(Keyspace keyspace, ClusterOperator clusterOperator) {
        String collectionPath = format(COLLECTION_PATH_TEMPLATE, keyspace.getBucket(), keyspace.getScope(), keyspace.getCollection());
        String documentIdRetrieveQuery = format(RETRIEVE_DOCUMENT_IDS_QUERY_TEMPLATE, collectionPath, whereClause);
        QueryResult documentIdsResult = clusterOperator.executeSingleSql(documentIdRetrieveQuery);
        return documentIdsResult.rowsAsObject()
                .stream()
                .map(jsonObject -> jsonObject.getString("id"))
                .collect(Collectors.toList());
    }
}


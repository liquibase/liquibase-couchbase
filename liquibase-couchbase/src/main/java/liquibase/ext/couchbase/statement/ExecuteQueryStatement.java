package liquibase.ext.couchbase.statement;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;

import com.couchbase.client.java.json.JsonObject;

import java.util.List;

import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.Param;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

/**
 * A statement to execute sql++/n1ql query
 * @see CouchbaseStatement
 * @see Keyspace
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ExecuteQueryStatement extends CouchbaseStatement {
    @NonNull
    private final String query;
    @NonNull
    private final List<Param> params;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        JsonObject jsonObject = JsonObject.create();
        if (CollectionUtils.isNotEmpty(params)) {
            params.forEach(param -> jsonObject.put(param.getName(), param.getValue()));
        }
        clusterOperator.getCluster().query(query, queryOptions().parameters(jsonObject));
    }
}

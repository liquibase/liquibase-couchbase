package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.types.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CreateQueryIndexStatement extends CouchbaseStatement {
    private final String bucketName;
    private final String indexName;
    private final List<Field> fields;
    private final CreateQueryIndexOptions options;

    @Override
    public void execute(CouchbaseConnection connection) {
        List<String> fieldList = fields.stream()
                .map(Field::getField)
                .collect(toList());
        connection.getCluster().queryIndexes().createIndex(bucketName, indexName, fieldList, options);
    }
}
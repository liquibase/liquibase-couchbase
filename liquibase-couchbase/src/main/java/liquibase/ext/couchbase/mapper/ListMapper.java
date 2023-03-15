package liquibase.ext.couchbase.mapper;

import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Document mapper for LIST mode (equals to cbimport LIST mode), when we have JsonArray with documents in file
 *
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html#list">cbimport documentation</a>
 */
@NoArgsConstructor
public class ListMapper implements DocFileMapper {
    @Override
    public List<Document> map(File file) {
        // TODO implement this in scope of the separate task
        return null;
    }
}

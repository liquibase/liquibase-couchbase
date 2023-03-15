package liquibase.ext.couchbase.mapper;

import java.util.List;

import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;

/**
 * Document mapper, transforms JSON file to set of Documents to import
 */
public interface DocFileMapper {

    List<Document> map(File file);
}

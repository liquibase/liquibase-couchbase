package liquibase.ext.couchbase.mapper;

import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.ImportFile;

import java.util.List;

/**
 * Document mapper, transforms JSON file to set of Documents to import
 */
public interface DocFileMapper {

    List<Document> map(ImportFile importFile);
}

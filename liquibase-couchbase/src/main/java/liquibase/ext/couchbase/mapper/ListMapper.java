package liquibase.ext.couchbase.mapper;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.provider.factory.DocumentKeyProviderFactory;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.ImportFile;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static liquibase.Scope.getCurrentScope;
import static liquibase.ext.couchbase.types.Document.document;


/**
 * Document mapper for LIST mode (equals to cbimport LIST mode), when we have JsonArray with documents in file
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html#list">cbimport documentation</a>
 */

public class ListMapper implements DocFileMapper {
    private final DocumentKeyProviderFactory keyProviderFactory;

    public ListMapper() {
        this(getCurrentScope().getSingleton(DocumentKeyProviderFactory.class));
    }

    public ListMapper(DocumentKeyProviderFactory keyProviderFactory) {
        this.keyProviderFactory = keyProviderFactory;
    }

    @Override
    public List<Document> map(ImportFile importFile) {
        List<Map<String, Object>> jsonsFromFile = importFile.readJsonList();
        DocumentKeyProvider keyProvider = keyProviderFactory.getKeyProvider(importFile);
        return extractDocuments(jsonsFromFile, keyProvider);
    }

    private List<Document> extractDocuments(List<Map<String, Object>> jsonsFromFile, DocumentKeyProvider keyProvider) {
        return jsonsFromFile.stream()
                .map(JsonObject::from)
                .map(doc -> document(keyProvider.getKey(doc), doc))
                .collect(toList());
    }
}

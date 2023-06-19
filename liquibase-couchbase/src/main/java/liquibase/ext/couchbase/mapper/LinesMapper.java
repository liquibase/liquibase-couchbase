package liquibase.ext.couchbase.mapper;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.provider.factory.DocumentKeyProviderFactory;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.ImportFile;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static liquibase.Scope.getCurrentScope;
import static liquibase.ext.couchbase.types.Document.document;

/**
 * Document mapper for LINES mode (equals to cbimport LINES mode), when every line in file consider as document
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html#list">cbimport documentation</a>
 */
public class LinesMapper implements DocFileMapper {
    private final DocumentKeyProviderFactory keyProviderFactory;

    public LinesMapper() {
        this(getCurrentScope().getSingleton(DocumentKeyProviderFactory.class));
    }

    public LinesMapper(DocumentKeyProviderFactory keyProviderFactory) {
        this.keyProviderFactory = keyProviderFactory;
    }

    @Override
    public List<Document> map(ImportFile importFile) {
        try (Stream<String> stream = importFile.lines()) {
            DocumentKeyProvider keyProvider = keyProviderFactory.getKeyProvider(importFile);
            return extractDocuments(stream, keyProvider);
        }
    }

    private List<Document> extractDocuments(Stream<String> stream, DocumentKeyProvider keyProvider) {
        return stream.map(JsonObject::fromJson)
                .map(json -> lineToDocument(keyProvider.getKey(json), json))
                .collect(toList());
    }


    private Document lineToDocument(String key, JsonObject json) {
        return document(key, json);
    }
}

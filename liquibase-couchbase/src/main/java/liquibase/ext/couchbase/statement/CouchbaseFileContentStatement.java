package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.deps.com.google.common.collect.ImmutableMap;
import liquibase.ext.couchbase.mapper.DocFileMapper;
import liquibase.ext.couchbase.mapper.LinesMapper;
import liquibase.ext.couchbase.mapper.ListMapper;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.ImportFile;
import liquibase.ext.couchbase.types.ImportType;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * A base class for import from file statements, generalize the logic of importing
 */
@Getter
public abstract class CouchbaseFileContentStatement extends CouchbaseTransactionStatement {

    private final Map<ImportType, DocFileMapper> importTypeToMapper =
            ImmutableMap.of(ImportType.LINES, new LinesMapper()
                    , ImportType.LIST, new ListMapper());

    protected List<Document> getDocsFromFile(ImportFile importFile) {
        return importTypeToMapper.get(importFile.getImportType()).map(importFile);
    }
}

package liquibase.ext.couchbase.change;

import com.couchbase.client.core.deps.com.google.common.collect.Lists;
import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.InsertDocumentsStatement;
import liquibase.ext.couchbase.statement.InsertFileContentStatement;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.ImportType;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.INSERT_MANY_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Document.document;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class InsertDocumentsChangeTest {

    private final Document DOC_1 = document("id1", "{key:value}", DataType.JSON);
    private final Document DOC_2 = document("id2", "{key2:value2}", DataType.JSON);

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_have_correct_change_type() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_MANY_TEST_XML);
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(InsertDocumentsChange.class);
    }

    @Test
    void Should_contains_correct_bucket() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_MANY_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getBucketName()).isEqualTo("testBucket");
    }

    @Test
    void Should_contains_specific_documents() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_MANY_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).containsExactly(DOC_1, DOC_2);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        InsertDocumentsChange change = createInsertDocumentChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION, Lists.newArrayList(DOC_1),
                null);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Documents inserted into collection %s", change.getCollectionName());
    }

    @Test
    void Should_generate_statement_correctly_documents_list() {
        InsertDocumentsChange change = createInsertDocumentChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION, Lists.newArrayList(DOC_1),
                null);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(InsertDocumentsStatement.class);

        InsertDocumentsStatement actualStatement = (InsertDocumentsStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
        assertThat(actualStatement.getDocuments()).isEqualTo(change.documents);
    }

    @Test
    void Should_generate_statement_correctly_file() {
        File file = File.builder().filePath("test").importType(ImportType.SAMPLE).build();
        InsertDocumentsChange change = createInsertDocumentChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION, null, file);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(InsertFileContentStatement.class);

        InsertFileContentStatement actualStatement = (InsertFileContentStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
        assertThat(actualStatement.getFile()).isEqualTo(change.file);
    }

    private InsertDocumentsChange createInsertDocumentChange(String bucketName, String scopeName, String collectionName,
                                                             List<Document> documents, File file) {
        return InsertDocumentsChange.builder()
                .bucketName(bucketName).scopeName(scopeName)
                .collectionName(collectionName).documents(documents)
                .file(file).build();
    }
}


package liquibase.ext.couchbase.change;

import com.google.common.collect.Lists;
import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.statement.UpsertDocumentsStatement;
import liquibase.ext.couchbase.statement.UpsertFileContentStatement;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.ImportType;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.UPSERT_MANY_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Document.document;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class UpsertDocumentsChangeTest {
    public final Document DOC_1 = document("id1", "{key:value}", DataType.JSON);
    public final Document DOC_2 = document("id2", "{key2:value2}", DataType.JSON);
    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(UPSERT_MANY_TEST_XML);
    }

    @Test
    void Should_contains_documents() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        UpsertDocumentsChange change = (UpsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).hasSize(2);
    }

    @Test
    void Should_contains_specific_document() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        UpsertDocumentsChange change = (UpsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).containsExactly(DOC_1, DOC_2);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        UpsertDocumentsChange change = createUpsertDocumentChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION, Lists.newArrayList(DOC_1),
                null);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Documents upserted into collection %s", change.getCollectionName());
    }

    @Test
    void Should_generate_statement_correctly_documents_list() {
        UpsertDocumentsChange change = createUpsertDocumentChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION, Lists.newArrayList(DOC_1),
                null);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(UpsertDocumentsStatement.class);

        UpsertDocumentsStatement actualStatement = (UpsertDocumentsStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
        assertThat(actualStatement.getDocuments()).isEqualTo(change.documents);
    }

    @Test
    void Should_generate_statement_correctly_file() {
        File file = File.builder().filePath("test").importType(ImportType.SAMPLE).build();
        UpsertDocumentsChange change = createUpsertDocumentChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION, null, file);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(UpsertFileContentStatement.class);

        UpsertFileContentStatement actualStatement = (UpsertFileContentStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
        assertThat(actualStatement.getFile()).isEqualTo(change.file);
    }

    private UpsertDocumentsChange createUpsertDocumentChange(String bucketName, String scopeName, String collectionName,
                                                             List<Document> documents, File file) {
        UpsertDocumentsChange change = new UpsertDocumentsChange();
        change.setBucketName(bucketName);
        change.setScopeName(scopeName);
        change.setCollectionName(collectionName);
        change.setDocuments(documents);
        change.setFile(file);
        return change;
    }
}
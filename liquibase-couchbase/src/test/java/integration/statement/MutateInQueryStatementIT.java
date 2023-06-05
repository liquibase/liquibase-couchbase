package integration.statement;

import com.couchbase.client.core.error.subdoc.PathExistsException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.MutateInQueryStatement;
import liquibase.ext.couchbase.transformer.MutateInSpecTransformer;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.couchbase.client.java.kv.MutateInOptions.mutateInOptions;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MutateInQueryStatementIT extends RandomizedScopeTestCase {

    private static final String DOC_FIELD_NAME = "field1";
    private static final String DOC_FIELD_Value = "val1";
    private static final String AGE_KEY = "age";
    private final TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName,
            scopeName);
    private final MutateInSpecTransformer mutateInSpecTransformer = new MutateInSpecTransformer();

    private Document doc1;
    private Document doc2;
    private Document doc3;
    private Keyspace keyspace;

    @BeforeEach
    void setUp() throws InterruptedException {
        doc1 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, DOC_FIELD_Value));
        doc2 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, DOC_FIELD_Value));
        doc3 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, "val5"));
        collectionOperator.insertDocs(doc1, doc2, doc3);
        collectionOperator.createPrimaryIndex(CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions().ignoreIfExists(true));
        TimeUnit.SECONDS.sleep(2L);
        keyspace = keyspace(bucketName, scopeName, collectionName);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        collectionOperator.removeDocs(doc1, doc2, doc3);
        collectionOperator.dropPrimaryIndex(DropPrimaryQueryIndexOptions.dropPrimaryQueryIndexOptions().ignoreIfNotExists(true));
        TimeUnit.SECONDS.sleep(2L);
    }

    @Test
    void Should_insert_property_by_provided_path_for_specific_query() {
        List<MutateInSpec> specs = getInsertSpec(AGE_KEY, "30");
        MutateIn mutate = MutateIn.builder().keyspace(keyspace).specs(specs).build();

        new MutateInQueryStatement(mutate, mutateInOptions().timeout(Duration.ofSeconds(2)), "field1=\"val1\"").execute(clusterOperator);
        Collection collection = collectionOperator.getCollection();
        assertThat(collection).extractingDocument(doc3.getId()).hasNoField(AGE_KEY);
        assertThat(collection).extractingDocument(doc1.getId()).hasField(AGE_KEY);
        assertThat(collection).extractingDocument(doc2.getId()).hasField(AGE_KEY);
    }

    @Test
    void Should_fail_if_specified_document_by_query_already_has_such_field() {
        List<MutateInSpec> specs = getInsertSpec(DOC_FIELD_NAME, DOC_FIELD_Value);
        MutateIn mutate = MutateIn.builder().keyspace(keyspace).specs(specs).build();

        assertThatExceptionOfType(PathExistsException.class).isThrownBy(
                () -> new MutateInQueryStatement(mutate, mutateInOptions().timeout(Duration.ofSeconds(2)), "field1=\"val1\"").execute(
                        clusterOperator)).withMessageContaining(
                "Path already exists in document");
    }

    private List<MutateInSpec> getInsertSpec(String path, String value) {
        return Arrays.asList(mutateInSpecTransformer.toSpec(
                new LiquibaseMutateInSpec(path, Arrays.asList(new Value(value, DataType.STRING)), MutateInType.INSERT)));
    }

}

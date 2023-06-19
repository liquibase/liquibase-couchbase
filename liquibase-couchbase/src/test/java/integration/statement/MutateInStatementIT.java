package integration.statement;

import com.couchbase.client.core.error.subdoc.PathExistsException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutateInSpec;
import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.MutateInStatement;
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

import static com.couchbase.client.java.kv.MutateInOptions.mutateInOptions;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MutateInStatementIT extends RandomizedScopeTestCase {
    private final TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName,
            scopeName);
    private final MutateInSpecTransformer mutateInSpecTransformer = new MutateInSpecTransformer();
    private Keyspace keyspace;
    private final Document doc1 = collectionOperator.generateTestDoc();
    private final Document doc2 = collectionOperator.generateTestDoc();

    @BeforeEach
    void setUp() {
        collectionOperator.insertDocs(doc1, doc2);
        keyspace = keyspace(bucketName, scopeName, collectionName);
    }

    @AfterEach
    void tearDown() {
        collectionOperator.removeDocs(doc1, doc2);
    }

    @Test
    void Should_insert_property_by_provided_path_for_specific_docId() {
        List<MutateInSpec> specs = getInsertSpec("age", "30");
        MutateIn mutate = MutateIn.builder().id(doc1.getId()).keyspace(keyspace).specs(specs).build();

        new MutateInStatement(mutate, mutateInOptions().timeout(Duration.ofSeconds(2))).execute(clusterOperator);
        Collection collection = collectionOperator.getCollection();
        assertThat(collection).extractingDocument(doc2.getId()).isJson().hasNoField("age");
        assertThat(collection).extractingDocument(doc1.getId()).isJson().hasField("age");
    }

    @Test
    void Should_fail_if_specified_document_already_has_such_field() {
        List<MutateInSpec> specs = getInsertSpec("field1", "value1");
        MutateIn mutate = MutateIn.builder().id(doc1.getId()).keyspace(keyspace).specs(specs).build();

        assertThatExceptionOfType(PathExistsException.class).isThrownBy(
                () -> new MutateInStatement(mutate, mutateInOptions().timeout(Duration.ofSeconds(2))).execute(clusterOperator)).withMessageContaining(
                "Path already exists in document");
    }

    private List<MutateInSpec> getInsertSpec(String path, String value) {
        return Arrays.asList(mutateInSpecTransformer.toSpec(
                new LiquibaseMutateInSpec(path, Arrays.asList(new Value(value, DataType.STRING)), MutateInType.INSERT)));
    }

}

package integration.statement;

import com.couchbase.client.core.error.subdoc.PathExistsException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutateInSpec;
import com.google.common.collect.ImmutableMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import common.BucketTestCase;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.MutateInStatement;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import static com.couchbase.client.java.json.JsonValue.jo;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_2;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MutateInStatementIT extends BucketTestCase {

    private Collection collection;
    private BucketOperator bucketOperator;
    private CollectionOperator collectionOperator;

    @BeforeEach
    void setUp() {
        bucketOperator = new BucketOperator(getBucket());
        collectionOperator = new CollectionOperator(bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
        collection = collectionOperator.getCollection();
        collectionOperator.insertDocs(testDocs());
    }

    @Test
    void Should_insert_property_by_provided_path_for_specific_docId() {
        List<MutateInSpec> specs = getInsertSpec("age", "30");
        MutateIn mutate = MutateIn.builder().id(TEST_ID).keyspace(TEST_KEYSPACE).specs(specs).build();

        new MutateInStatement(mutate).execute(new ClusterOperator(cluster));

        assertThat(collection).extractingDocument(TEST_ID_2).hasNoField("age");
        assertThat(collection).extractingDocument(TEST_ID).itsContentEquals(
                jo().put("name", "Roman").put("age", "30")
        );
    }

    @AfterEach
    void tearDown() {
        collectionOperator.removeDocs(TEST_ID, TEST_ID_2);
    }

    @Test
    void Should_fail_if_specified_document_already_has_such_field() {
        List<MutateInSpec> specs = getInsertSpec("name", "roman");
        MutateIn mutate = MutateIn.builder().id(TEST_ID).keyspace(TEST_KEYSPACE).specs(specs).build();

        assertThatExceptionOfType(PathExistsException.class)
                .isThrownBy(() -> new MutateInStatement(mutate).execute(new ClusterOperator(cluster)))
                .withMessageContaining("Path already exists in document");
    }

    private List<MutateInSpec> getInsertSpec(String path, String value) {
        return Arrays.asList(
                new LiquibaseMutateInSpec(path, value, MutateInType.INSERT)
                        .toSpec()
        );
    }

    private Map<String, JsonObject> testDocs() {
        JsonObject roman = jo().put("name", "Roman");
        JsonObject alex = jo().put("name", "Alex");
        return ImmutableMap.of(TEST_ID, roman, TEST_ID_2, alex);
    }
}

package liquibase.ext.couchbase.change;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.bucket.CompressionMode;
import com.couchbase.client.java.manager.bucket.ConflictResolutionType;
import com.couchbase.client.java.manager.bucket.EvictionPolicyType;
import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.change.utils.BucketCreationMapper;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.statement.CreateBucketStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_CHANGE_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class CreateBucketChangeTest {

    private ChangeLogProvider changeLogProvider;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase db = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(db);
    }

    @Test
    void Should_parse_changes_correctly() {
        CreateBucketChange createBucketChange = createCreateBucketChange(TEST_BUCKET);

        DatabaseChangeLog load = changeLogProvider.load(CREATE_BUCKET_CHANGE_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(CreateBucketChange.class::cast)
                .containsExactly(createBucketChange);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        CreateBucketChange change = createCreateBucketChange(TEST_BUCKET);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Bucket [%s] has been created", change.getBucketName());
    }

    @Test
    void Should_generate_statement_correctly() {
        CreateBucketChange change = createCreateBucketChange(TEST_BUCKET);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(CreateBucketStatement.class);

        CreateBucketStatement actualStatement = (CreateBucketStatement) statements[0];
        BucketCreationMapper bucketCreationMapper = new BucketCreationMapper(change);
        assertThat(actualStatement.getSettings().toString()).isEqualTo(
                bucketCreationMapper.bucketSettings().toString()); // Equals of object is not overridden
    }

    @Test
    void Should_generate_inverse_correctly() {
        CreateBucketChange change = createCreateBucketChange(TEST_BUCKET);

        Change[] inverses = change.createInverses();

        assertThat(inverses).hasSize(1);
        assertThat(inverses[0]).isInstanceOf(DropBucketChange.class);

        DropBucketChange inverseChange = (DropBucketChange) inverses[0];
        assertThat(inverseChange.getBucketName()).isEqualTo(change.getBucketName());

    }

    private CreateBucketChange createCreateBucketChange(String bucketName) {
        return CreateBucketChange.builder().bucketName(bucketName)
                .bucketType(BucketType.COUCHBASE).compressionMode(CompressionMode.OFF)
                .conflictResolutionType(ConflictResolutionType.TIMESTAMP).evictionPolicy(EvictionPolicyType.FULL)
                .flushEnabled(true).minimumDurabilityLevel(DurabilityLevel.NONE)
                .numReplicas(0).maxExpiryInHours(1L).ramQuotaMB(128L)
                .replicaIndexes(false).storageBackend("couchstore")
                .timeoutInSeconds(10L).build();
    }


}
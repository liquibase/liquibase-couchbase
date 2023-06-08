package liquibase.ext.couchbase.change;

import com.couchbase.client.java.kv.StoreSemantics;
import com.google.common.collect.Lists;
import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.MutateInQueryStatement;
import liquibase.ext.couchbase.statement.MutateInStatement;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INSERT_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_3;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static java.util.Arrays.asList;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
public class MutateInChangeTest {

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_parse_mutateIn_id_correctly() {
        DatabaseChangeLog changeLog = changeLogProvider.load(MUTATE_IN_INSERT_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());

        assertThat(changeSet.getChanges())
                .map(MutateInChange.class::cast)
                .containsExactly(
                        changeWithId(asList(spec("user.age", "29", DataType.STRING, MutateInType.INSERT))),
                        changeWithWhereClause(
                                asList(spec("adoc", "{\"newDocumentField\": \"newDocumentValue\"}", DataType.JSON, MutateInType.REPLACE)))
                );
    }

    @Test
    void Should_has_correct_confirm_msg() {
        DatabaseChangeLog changeLog = changeLogProvider.load(MUTATE_IN_INSERT_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        Change change = firstOf(changeSet.getChanges());

        assertThat(change.getConfirmationMessage())
                .isEqualTo("MutateIn %s operations has been successfully executed", 1);
    }

    @Test
    void Should_generate_statement_correctly_with_id() {
        MutateInChange change = changeWithId(Lists.newArrayList(
                new LiquibaseMutateInSpec("test", Lists.newArrayList(new Value("data", DataType.STRING)), MutateInType.INSERT)));

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(MutateInStatement.class);

        MutateInStatement actualStatement = (MutateInStatement) statements[0];
        assertThat(actualStatement.getMutate().getId()).isEqualTo(change.getId());
        assertThat(actualStatement.getMutate().getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }

    @Test
    void Should_generate_statement_correctly_with_where() {
        MutateInChange change = changeWithWhereClause(Lists.newArrayList(
                new LiquibaseMutateInSpec("test", Lists.newArrayList(new Value("data", DataType.STRING)), MutateInType.INSERT)));

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(MutateInQueryStatement.class);

        MutateInQueryStatement actualStatement = (MutateInQueryStatement) statements[0];

        assertThat(actualStatement.getWhereClause()).isEqualTo(change.getWhereCondition());
        assertThat(actualStatement.getMutate().getId()).isEqualTo(change.getId());
        assertThat(actualStatement.getMutate().getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }

    private LiquibaseMutateInSpec spec(String path, String value, DataType dataType, MutateInType type) {
        return new LiquibaseMutateInSpec(path, Arrays.asList(new Value(value, dataType)), type);
    }

    private MutateInChange changeWithId(List<LiquibaseMutateInSpec> specs) {
        return new MutateInChange(
                TEST_ID,
                null,
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                "PT1H",
                true,
                StoreSemantics.INSERT,
                specs
        );
    }

    private MutateInChange changeWithWhereClause(List<LiquibaseMutateInSpec> specs) {
        return new MutateInChange(
                null,
                "aKey=\"avalue\"",
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION_3,
                "PT1H",
                true,
                StoreSemantics.REPLACE,
                specs
        );
    }
}
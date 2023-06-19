package liquibase.ext.couchbase.change;

import com.couchbase.client.java.kv.StoreSemantics;
import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.MutateInQueryStatement;
import liquibase.ext.couchbase.statement.MutateInSqlQueryStatement;
import liquibase.ext.couchbase.statement.MutateInStatement;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INSERT_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_3;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static java.util.Collections.singletonList;
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
                        changeWithId(singletonList(spec("user.age", "29", DataType.STRING, MutateInType.INSERT))),
                        changeWithWhereClause(singletonList(
                                spec("adoc", "{\"newDocumentField\": \"newDocumentValue\"}", DataType.JSON, MutateInType.REPLACE))),
                        changeWithSqlPlusPlusQuery(singletonList(spec("user.age", "50", DataType.STRING, MutateInType.INSERT)))
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
    void Should_generate_statement_correctly() {
        MutateInChange change = changeWithQuery(singletonList(
                new LiquibaseMutateInSpec("test", singletonList(new Value("data", DataType.STRING)), MutateInType.INSERT)));

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(MutateInSqlQueryStatement.class);

        MutateInSqlQueryStatement actualStatement = (MutateInSqlQueryStatement) statements[0];
        MutateIn mutateIn = actualStatement.getMutate();
        assertThat(mutateIn.getId()).isNull();
        assertThat(mutateIn.getKeyspace()).isEqualTo(keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
        assertThat(actualStatement.getSqlPlusPlusQuery()).isEqualTo(change.getSqlPlusPlusQuery());
    }

    @Test
    void Should_generate_statement_correctly_with_id() {
        MutateInChange change = changeWithId(singletonList(
                new LiquibaseMutateInSpec("test", singletonList(new Value("data", DataType.STRING)), MutateInType.INSERT)));

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(MutateInStatement.class);

        MutateInStatement actualStatement = (MutateInStatement) statements[0];
        MutateIn mutateIn = actualStatement.getMutate();
        assertThat(mutateIn.getId()).isEqualTo(change.getId());
        assertThat(mutateIn.getKeyspace()).isEqualTo(keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }

    @Test
    void Should_generate_statement_correctly_with_where() {
        MutateInChange change = changeWithWhereClause(
                singletonList(new LiquibaseMutateInSpec("test", singletonList(new Value("data", DataType.STRING)), MutateInType.INSERT)));

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(MutateInQueryStatement.class);

        MutateInQueryStatement actualStatement = (MutateInQueryStatement) statements[0];
        MutateIn mutateIn = actualStatement.getMutate();
        assertThat(actualStatement.getWhereClause()).isEqualTo(change.getWhereCondition());
        assertThat(mutateIn.getId()).isEqualTo(change.getId());
        assertThat(mutateIn.getKeyspace()).isEqualTo(keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }

    private LiquibaseMutateInSpec spec(String path, String value, DataType dataType, MutateInType type) {
        return new LiquibaseMutateInSpec(path, singletonList(new Value(value, dataType)), type);
    }

    private MutateInChange changeWithQuery(List<LiquibaseMutateInSpec> specs) {
        return new MutateInChange(
                null,
                null,
                "sqlPlusPlusQuery",
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                "PT1H",
                true,
                StoreSemantics.INSERT,
                specs
        );
    }

    private MutateInChange changeWithId(List<LiquibaseMutateInSpec> specs) {
        return new MutateInChange(
                TEST_ID,
                null,
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
                null,
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION_3,
                "PT1H",
                true,
                StoreSemantics.REPLACE,
                specs
        );
    }

    private MutateInChange changeWithSqlPlusPlusQuery(List<LiquibaseMutateInSpec> specs) {
        return new MutateInChange(
                null,
                null,
                "SELECT meta().id FROM `testBucket`.`testScope`.`testCollection`",
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                "PT1H",
                true,
                StoreSemantics.INSERT,
                specs
        );
    }
}
package integration.precondition;

import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.exception.precondition.SqlCheckPreconditionException;
import liquibase.ext.couchbase.precondition.SqlCheckPrecondition;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SqlCheckPreconditionIT extends RandomizedScopeTestCase {

    @Test
    void Should_not_throw_when_sql_precondition_result_as_expected() {
        SqlCheckPrecondition sqlCheckPrecondition = createPreconditionWithParams("{\"count\": 1}", TEST_BUCKET);

        assertDoesNotThrow(() -> sqlCheckPrecondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_when_sql_precondition_result_unexpected() {
        String expectedJsonWrong = "{\"ab\" : \"15\"}";
        SqlCheckPrecondition sqlCheckPrecondition = createPreconditionWithParams(expectedJsonWrong, TEST_BUCKET);

        assertThatExceptionOfType(SqlCheckPreconditionException.class)
                .isThrownBy(() -> sqlCheckPrecondition.check(database, null, null, null))
                .withMessage("Result of [%s] query is differ then expected[%s]", sqlCheckPrecondition.getQuery(), expectedJsonWrong);
    }

    private SqlCheckPrecondition createPreconditionWithParams(String expectedResult, String query) {
        SqlCheckPrecondition precondition = new SqlCheckPrecondition();
        String bucketExistsQueryTemplate = "SELECT COUNT(*) as count FROM system:keyspaces WHERE name = \"%s\"";
        precondition.setQuery(format(bucketExistsQueryTemplate, query));
        precondition.setExpectedResultJson(expectedResult);
        return precondition;
    }
}

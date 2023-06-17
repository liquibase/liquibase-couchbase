package integration.precondition;

import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.exception.precondition.SqlCheckCountPreconditionException;
import liquibase.ext.couchbase.precondition.QueryCountCheckPrecondition;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class QueryCountCheckPreconditionIT extends RandomizedScopeTestCase {

    @Test
    void Should_not_throw_when_sql_precondition_result_as_expected() {
        QueryCountCheckPrecondition queryCountCheckPrecondition = createPreconditionWithParams(1, TEST_BUCKET);

        assertDoesNotThrow(() -> queryCountCheckPrecondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_when_sql_precondition_result_unexpected() {
        Integer expectedWrongResult = 15;
        QueryCountCheckPrecondition sqlCheckPrecondition = createPreconditionWithParams(expectedWrongResult, TEST_BUCKET);

        assertThatExceptionOfType(SqlCheckCountPreconditionException.class)
                .isThrownBy(() -> sqlCheckPrecondition.check(database, null, null, null))
                .withMessage("Sql precondition query [%s] result is different then expected count [%d]", sqlCheckPrecondition.getQuery(),
                        expectedWrongResult);
    }

    private QueryCountCheckPrecondition createPreconditionWithParams(Integer expectedResult, String bucketName) {
        String bucketExistsQueryTemplate = "SELECT COUNT(*) as count FROM system:keyspaces WHERE name = \"%s\"";
        return new QueryCountCheckPrecondition(expectedResult, format(bucketExistsQueryTemplate, bucketName));
    }
}
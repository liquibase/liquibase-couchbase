package common.operators;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.types.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TestCollectionOperatorTest {
    private static final int MAX_FIELDS_IN_DOC = 3;
    private static final String TEST_DOC_ID = "docId";
    @InjectMocks
    private TestCollectionOperator testCollectionOperator;

    @Test
    void should_generate_test_doc() {
        Document result = testCollectionOperator.generateTestDoc();

        String id = result.getId();
        assertThat(id).contains(TEST_DOC_ID);
        assertThat(id.replace(TEST_DOC_ID + "_", EMPTY)).isAlphanumeric();
        JsonObject content = result.getContentAsJson();
        for (int i = 1; i < MAX_FIELDS_IN_DOC; i++) {
            String fieldName = "field" + i;
            assertTrue(content.containsKey(fieldName));
            String fieldValue = "value" + i;
            assertThat((String) content.get(fieldName)).isEqualTo(fieldValue);
        }
    }
}
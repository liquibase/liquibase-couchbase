package stress;

import liquibase.Liquibase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import system.LiquiBaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.INSERT_UPSERT_STRESS_TEST_XML;
import static java.lang.String.format;

@Slf4j
public class InsertUpsertStressTest extends LiquiBaseSystemTest {

    @Test
    @SneakyThrows
    void Bucket_should_be_created() {
        Liquibase liquibase = liquiBase(INSERT_UPSERT_STRESS_TEST_XML);
        long startTime = System.currentTimeMillis();
        liquibase.update();
        long endTime = System.currentTimeMillis();
        log.info(format("Stress test has completed in {%d} milliseconds", endTime - startTime));
    }

}

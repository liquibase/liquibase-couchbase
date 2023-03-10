package stress;

import liquibase.Liquibase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.INSERT_UPSERT_STRESS_TEST_XML;
import static java.lang.String.format;

@Slf4j
public class InsertUpsertStressTest extends LiquibaseSystemTest {

    //@Test //TODO do we need this test every time? it fails because of RAM quota
    @SneakyThrows
    void Bucket_should_be_created() {
        Liquibase liquibase = liquibase(INSERT_UPSERT_STRESS_TEST_XML);
        long startTime = System.currentTimeMillis();
        liquibase.update();
        long endTime = System.currentTimeMillis();
        log.info(format("Stress test has completed in {%d} milliseconds", endTime - startTime));
    }

}

package liquibase.ext.couchbase.mapper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import liquibase.ContextExpression;
import liquibase.Labels;
import liquibase.change.CheckSum;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.ext.couchbase.changelog.Context;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeSetMapperTest {

    @Test
    void Should_map_to_ran_change_set() {
        CouchbaseChangeLog changeLog = new CouchbaseChangeLog();
        changeLog.setFileName("fileName");
        changeLog.setId("id");
        changeLog.setAuthor("author");
        changeLog.setCheckSum("7:2cdf9876e74347162401315d34b83746");
        Date date = new Date(2023, Calendar.MAY, 25, 12, 1, 2);
        changeLog.setDateExecuted("2023.05.25 12:01:02");
        changeLog.setTag("tag");
        changeLog.setExecType(ChangeSet.ExecType.EXECUTED);
        changeLog.setDescription("description");
        changeLog.setComments("comments");
        changeLog.setDeploymentId("deploymentId");
        changeLog.setLabels(Stream.of("label1", "label2", "label3").collect(Collectors.toSet()));
        Context context = new Context(new ContextExpression("context"));
        changeLog.setContext(context);
        changeLog.setOrderExecuted(999);
        changeLog.setLiquibaseVersion("version");

        RanChangeSet expected = new RanChangeSet(
                changeLog.getFileName(),
                changeLog.getId(),
                changeLog.getAuthor(),
                CheckSum.parse(changeLog.getCheckSum()),
                date,
                changeLog.getTag(),
                changeLog.getExecType(),
                changeLog.getDescription(),
                changeLog.getComments(),
                new ContextExpression(changeLog.getContext().getOriginalString()),
                new Labels(changeLog.getLabels()),
                changeLog.getDeploymentId()
        );
        expected.setOrderExecuted(changeLog.getOrderExecuted());
        expected.setLiquibaseVersion(changeLog.getLiquibaseVersion());

        RanChangeSet result =  ChangeSetMapper.mapToRanChangeSet(changeLog);
        assertThat(result).isEqualTo(expected);
    }
}

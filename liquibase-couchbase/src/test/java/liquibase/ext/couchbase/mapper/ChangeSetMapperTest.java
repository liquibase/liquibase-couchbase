package liquibase.ext.couchbase.mapper;

import liquibase.ContextExpression;
import liquibase.Labels;
import liquibase.change.CheckSum;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.ext.couchbase.changelog.Context;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import liquibase.util.LiquibaseUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockitoSettings
class ChangeSetMapperTest {

    @Test
    void Should_map_to_ran_change_set() {
        Date date = new Date(2023, Calendar.MAY, 25, 12, 1, 2);
        String dateStr = "2023.05.25 12:01:02";
        Context context = new Context(new ContextExpression("context"));

        CouchbaseChangeLog changeLog = Mockito.mock(CouchbaseChangeLog.class);
        when(changeLog.getFileName()).thenReturn("fileName");
        when(changeLog.getId()).thenReturn("id");
        when(changeLog.getAuthor()).thenReturn("author");
        when(changeLog.getCheckSum()).thenReturn("7:2cdf9876e74347162401315d34b83746");
        when(changeLog.getDateExecuted()).thenReturn(dateStr);
        when(changeLog.getTag()).thenReturn("tag");
        when(changeLog.getExecType()).thenReturn(ChangeSet.ExecType.EXECUTED);
        when(changeLog.getDescription()).thenReturn("description");
        when(changeLog.getComments()).thenReturn("comments");
        when(changeLog.getDeploymentId()).thenReturn("deploymentId");
        when(changeLog.getLabels()).thenReturn(Stream.of("label1", "label2", "label3").collect(Collectors.toSet()));
        when(changeLog.getContext()).thenReturn(context);
        when(changeLog.getOrderExecuted()).thenReturn(999);
        when(changeLog.getLiquibaseVersion()).thenReturn("version");

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

        RanChangeSet result = ChangeSetMapper.mapToRanChangeSet(changeLog);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void Should_map_to_couchbase_change_log() {
        String liquibaseVersion = LiquibaseUtil.getBuildVersion();
        String dateStr = "2023.05.25 12:01:02";

        CheckSum checkSum = CheckSum.parse("7:2cdf9876e74347162401315d34b83746");

        Labels labels = Mockito.mock(Labels.class);
        when(labels.getLabels()).thenReturn(Stream.of("label1", "label2", "label3").collect(Collectors.toSet()));

        ContextExpression contextExpression = Mockito.mock(ContextExpression.class);

        ChangeSet changeSet = Mockito.mock(ChangeSet.class);
        when(changeSet.getFilePath()).thenReturn("filePath");
        when(changeSet.getId()).thenReturn("id");
        when(changeSet.getAuthor()).thenReturn("author");
        when(changeSet.generateCheckSum()).thenReturn(checkSum);
        when(changeSet.getDescription()).thenReturn("description");
        when(changeSet.getComments()).thenReturn("comments");
        when(changeSet.getLabels()).thenReturn(labels);
        when(changeSet.getContextFilter()).thenReturn(contextExpression);

        CouchbaseChangeLog expected = CouchbaseChangeLog.builder()
                .fileName(changeSet.getFilePath())
                .id(changeSet.getId())
                .author(changeSet.getAuthor())
                .checkSum(checkSum.toString())
                .dateExecuted(dateStr)
                .description(changeSet.getDescription())
                .comments(changeSet.getComments())
                .labels(changeSet.getLabels().getLabels())
                .context(new Context(changeSet.getContextFilter()))
                .liquibaseVersion(liquibaseVersion)
                .build();

        CouchbaseChangeLog result = ChangeSetMapper.mapToCouchbaseChangeLog(changeSet);
        result.setDateExecuted(dateStr);
        assertThat(result).isEqualTo(expected);
    }
}

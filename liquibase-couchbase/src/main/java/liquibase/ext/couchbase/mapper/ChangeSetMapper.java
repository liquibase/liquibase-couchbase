package liquibase.ext.couchbase.mapper;

import liquibase.Labels;
import liquibase.change.CheckSum;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import liquibase.util.LiquibaseUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class ChangeSetMapper {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    @SneakyThrows
    public static RanChangeSet mapToRanChangeSet(CouchbaseChangeLog changeLog) {
        RanChangeSet ranChangeSet = new RanChangeSet(
                changeLog.getFileName(),
                changeLog.getId(),
                changeLog.getAuthor(),
                CheckSum.parse(changeLog.getCheckSum()),
                dateFormat.parse(changeLog.getDateExecuted()),
                changeLog.getTag(),
                changeLog.getExecType(),
                changeLog.getDescription(),
                changeLog.getComments(),
                null,
                new Labels(changeLog.getLabels()),
                changeLog.getDeploymentId()
        );
        ranChangeSet.setOrderExecuted(changeLog.getOrderExecuted());
        ranChangeSet.setLiquibaseVersion(changeLog.getLiquibaseVersion());
        return ranChangeSet;
    }

    public CouchbaseChangeLog mapToCouchbaseChangeLog(ChangeSet changeSet) {
        return CouchbaseChangeLog.builder()
                .fileName(changeSet.getFilePath())
                .id(changeSet.getId())
                .author(changeSet.getAuthor())
                .checkSum(changeSet.generateCheckSum().toString())
                .dateExecuted(dateFormat.format(new Date()))
                .description(changeSet.getDescription())
                .comments(changeSet.getComments())
                .labels(changeSet.getLabels().getLabels())
                .liquibaseVersion(LiquibaseUtil.getBuildVersion()).build();
        // TODO why changeSet.getInheritableContextFilter() ?
    }

}

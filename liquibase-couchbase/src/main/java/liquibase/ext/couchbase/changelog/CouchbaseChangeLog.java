package liquibase.ext.couchbase.changelog;

import liquibase.changelog.ChangeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouchbaseChangeLog {
    private String fileName;
    private String id;
    private String author;
    private String checkSum;
    private String dateExecuted;
    private String tag;
    private ChangeSet.ExecType execType;
    private String description;
    private String comments;
    private int orderExecuted;
    private Set<String> labels;
    private String deploymentId;
    private String liquibaseVersion;
}

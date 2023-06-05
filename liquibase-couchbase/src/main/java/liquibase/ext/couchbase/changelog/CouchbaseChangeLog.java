package liquibase.ext.couchbase.changelog;

import java.util.Set;

import liquibase.changelog.ChangeSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
    private Set<String> labels;
    private Context context;
    private int orderExecuted;
    private String deploymentId;
    private String liquibaseVersion;
}

package liquibase.ext.couchbase.changelog;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import liquibase.changelog.ChangeSet;
import liquibase.ext.couchbase.serializer.LocalDateTimeDeserializer;
import liquibase.ext.couchbase.serializer.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateExecuted;
    private String tag;
    private ChangeSet.ExecType execType;
    private String description;
    private String comments;
    private int orderExecuted;
    private Set<String> labels;
    private String deploymentId;
    private String liquibaseVersion;
}

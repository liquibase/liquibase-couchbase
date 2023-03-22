package liquibase.ext.couchbase.reader;

import com.google.common.io.CharStreams;
import liquibase.Scope;
import liquibase.SingletonObject;
import liquibase.exception.ChangeLogParseException;
import liquibase.resource.Resource;
import liquibase.resource.ResourceAccessor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class N1qlFileReader implements SingletonObject {

    @SneakyThrows
    public String load(String physicalChangeLogLocation) {
        ResourceAccessor resourceAccessor = Scope.getCurrentScope().getResourceAccessor();
        Resource changelog = resourceAccessor.get(physicalChangeLogLocation);
        if (!changelog.exists()) {
            throw new ChangeLogParseException(physicalChangeLogLocation + " does not exist");
        }
        try (InputStream changeLogStream = changelog.openInputStream()) {
            return CharStreams.toString(new InputStreamReader(changeLogStream));
        }
    }

    public List<String> retrieveQueriesFromSqlChangelog(String changeLog) {
        StringBuilder changeLogWithoutComments = new StringBuilder();

        for (String line : changeLog.split("\n")) {
            if (!line.startsWith("--")) {
                changeLogWithoutComments.append(line).append("\n");
            }
        }

        return Arrays.stream(changeLogWithoutComments.toString().split(";"))
                .map(s -> s.replace("\n", " ").trim())
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());
    }
}

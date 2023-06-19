package liquibase.ext.couchbase.reader;

import com.couchbase.client.core.deps.com.google.common.base.Splitter;
import liquibase.SingletonObject;
import liquibase.exception.ChangeLogParseException;
import liquibase.resource.Resource;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;

/**
 * Couchbase query extractor from {@link Resource}
 */
public class SqlFileReader implements SingletonObject {

    /**
     * Pattern for detect any kind of comments
     */
    private final Pattern removeCommentsPattern = compile("\\/\\*.*?\\*\\/|--.*?\\n");

    /**
     * Splitting couchbase statement by semicolon which is not surrounded by any kind of quotes
     */
    private final Splitter statementsSplitter = Splitter.on(compile(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));

    /**
     * @param resource Resource to open
     * @return {@link List} list of parsed queries
     *
     * @throws ChangeLogParseException if file is not exists
     */
    @SneakyThrows
    public List<String> readQueries(Resource resource) {
        if (!resource.exists()) {
            throw new ChangeLogParseException(resource.getPath() + " does not exist");
        }

        try (InputStream is = resource.openInputStream()) {
            String fileContent = IOUtils.toString(is, UTF_8);
            return retrieveQueries(fileContent);
        }
    }

    private List<String> retrieveQueries(String fileContent) {
        String withoutComments = removeCommentsPattern.matcher(fileContent).replaceAll("");

        return statementsSplitter.splitToList(withoutComments).stream()
                .filter(StringUtils::isNotBlank)
                .collect(toList());
    }

}

package liquibase.ext.couchbase.exception;

import static java.lang.String.format;

/**
 * Used to indicate various file import errors during import documents from file
 */
public class IncorrectFileException extends RuntimeException {

    private static final String template = "File [%s] format incorrect";

    public IncorrectFileException(String filePath) {
        super(format(template, filePath));
    }
}

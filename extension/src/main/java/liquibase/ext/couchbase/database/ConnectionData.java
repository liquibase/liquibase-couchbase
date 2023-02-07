package liquibase.ext.couchbase.database;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Encapsulates common data for connection to couchbase cluster
 */
@Data
@RequiredArgsConstructor
public class ConnectionData {
    private final String user;
    private final String password;
    private final String connectionString;
}

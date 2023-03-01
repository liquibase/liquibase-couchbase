package liquibase.ext.couchbase.lockservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity for handling lock specific {@link com.couchbase.client.java.Bucket Bucket}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouchbaseLock {

    /**
     * An ID of the underlying lock bucket
     */
    @NonNull
    private String id;
    /**
     * Instance of {@link liquibase.lockservice.LockService LockService} who owns that lock
     */
    @NonNull
    private String owner;
    /**
     * Timestamp in milliseconds
     */
    @NonNull
    private Long lockedAt;

}

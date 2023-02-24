package liquibase.ext.couchbase.lockservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class CouchbaseLock {

    private String id;
    /**
     * Timestamp in milliseconds
     */
    @NonNull
    private Long lockedAt;

}

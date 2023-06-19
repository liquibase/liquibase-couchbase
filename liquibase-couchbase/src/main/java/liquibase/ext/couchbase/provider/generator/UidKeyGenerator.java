package liquibase.ext.couchbase.provider.generator;

import java.util.UUID;

public class UidKeyGenerator implements KeyGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}

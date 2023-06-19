package liquibase.ext.couchbase.provider.generator;

import java.util.concurrent.atomic.AtomicLong;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IncrementalKeyGenerator implements KeyGenerator {

    private final AtomicLong lastValue;

    public IncrementalKeyGenerator() {
        this(new AtomicLong());
    }

    @Override
    public String generate() {
        return String.valueOf(lastValue.getAndIncrement());
    }
}

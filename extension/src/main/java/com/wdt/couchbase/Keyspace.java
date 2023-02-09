package com.wdt.couchbase;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Keyspace {

    private final String bucket;
    private final String scope;
    private final String collection;

    public static Keyspace keyspace(@NonNull String bucket,
                                    @NonNull String scope,
                                    @NonNull String collection) {
        return new Keyspace(bucket, scope, collection);
    }


    public String getKeyspace() {
        return String.format("`%s`.`%s`.`%s`", bucket, scope, collection);
    }

}

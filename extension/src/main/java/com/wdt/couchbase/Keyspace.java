package com.wdt.couchbase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Keyspace {

    @NonNull
    private String bucket;
    private String scope = "_default";
    private String collection = "_default";

    public static Keyspace keyspace(String bucket, String scope, String collection) {
        return new Keyspace(bucket, scope, collection);
    }

    public String getKeyspace() {
        return String.format("`%s`.`%s`.`%s`", bucket, scope, collection);
    }

}

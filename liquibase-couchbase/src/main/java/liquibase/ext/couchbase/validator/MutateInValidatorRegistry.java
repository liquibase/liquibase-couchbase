package liquibase.ext.couchbase.validator;

import java.util.EnumMap;
import java.util.Map;

import liquibase.SingletonObject;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_APPEND;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_CREATE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_INSERT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_INSERT_UNIQUE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_PREPEND;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.DECREMENT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.INCREMENT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.INSERT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.REMOVE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.REPLACE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.UPSERT;

public class MutateInValidatorRegistry implements SingletonObject {

    private final Map<MutateInType, MutateInValidator> map;

    public MutateInValidatorRegistry() {
        map = new EnumMap<>(MutateInType.class);
        map.put(REMOVE, new MutateInRemoveValidator(REMOVE));
        map.put(REPLACE, new MutateInReplaceValidator(REPLACE));
        map.put(INCREMENT, new MutateInLongValueValidator(INCREMENT));
        map.put(DECREMENT, new MutateInLongValueValidator(DECREMENT));
        map.put(ARRAY_APPEND, new MutateInArrayValidator(ARRAY_APPEND));
        map.put(ARRAY_CREATE, new MutateInArrayValidator(ARRAY_CREATE));
        map.put(ARRAY_INSERT, new MutateInArrayValidator(ARRAY_INSERT));
        map.put(ARRAY_PREPEND, new MutateInArrayValidator(ARRAY_PREPEND));
        map.put(INSERT, new MutateInInsertUpsertUniqueValidator(INSERT));
        map.put(UPSERT, new MutateInInsertUpsertUniqueValidator(UPSERT));
        map.put(ARRAY_INSERT_UNIQUE, new MutateInInsertUpsertUniqueValidator(ARRAY_INSERT_UNIQUE));
    }

    public MutateInValidator get(MutateInType type) {
        return ofNullable(map.get(type))
                .orElseThrow(() -> new IllegalArgumentException(format("Type %s is not supported ", type)));
    }

}

package liquibase.ext.couchbase.types.subdoc;

import com.couchbase.client.java.kv.MutateInSpec;

import java.util.function.BiFunction;

/**
 * Factory for {@link MutateInSpec} from xml enumeration <br>
 * Association between xml enumeration to MutateInSpec
 */
public enum MutateInType {

    INSERT((path, value) -> MutateInSpec.insert(path, value));
    //TODO upsert,arrays,etc

    private final BiFunction<String, Object, MutateInSpec> factoryMethod;

    MutateInType(BiFunction<String, Object, MutateInSpec> consumer) {
        this.factoryMethod = consumer;
    }

    public MutateInSpec toMutateInSpec(String path, Object value) {
        return factoryMethod.apply(path,value);
    }

}

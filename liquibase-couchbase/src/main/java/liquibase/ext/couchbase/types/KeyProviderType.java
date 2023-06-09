package liquibase.ext.couchbase.types;

import liquibase.SingletonObject;
import lombok.Getter;

/**
 * Type of key for document provider
 */
@Getter
public enum KeyProviderType implements SingletonObject {
    DEFAULT("DEFAULT"),
    UID("UID"),
    INCREMENT("INCREMENT"),
    EXPRESSION("EXPRESSION");

    private final String name;

    KeyProviderType(String name) {
        this.name = name;
    }

}

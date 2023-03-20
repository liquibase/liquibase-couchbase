package liquibase.ext.couchbase.types;

import liquibase.SingletonObject;
import lombok.Getter;

/**
 * Types of import from file (based on cbimport tool formats)
 *
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html#dataset-formats"/>
 */
@Getter
public enum ImportType implements SingletonObject {
    LINES("LINES"),
    LIST("LIST"),
    SAMPLE("SAMPLE"),
    KEY_GENERATORS("KEY_GENERATORS");

    private final String name;

    ImportType(String name) {
        this.name = name;
    }
}

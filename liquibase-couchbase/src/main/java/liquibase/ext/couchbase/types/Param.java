package liquibase.ext.couchbase.types;

import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Param for query
 *
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Param extends AbstractLiquibaseSerializable {

  private String name;
  private Object value;

  @Override
  public String getSerializedObjectName() {
    return "param";
  }

  @Override
  public String getSerializedObjectNamespace() {
    return STANDARD_CHANGELOG_NAMESPACE;
  }

  @Override
  public SerializationType getSerializableFieldType(String field) {
    return SerializationType.DIRECT_VALUE;
  }
}

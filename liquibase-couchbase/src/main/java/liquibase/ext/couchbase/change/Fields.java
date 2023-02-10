package liquibase.ext.couchbase.change;

import java.util.List;

import liquibase.parser.core.ParsedNode;
import liquibase.parser.core.ParsedNodeException;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class Fields extends AbstractLiquibaseSerializable {

    @Getter
    private List<String> fields;

    @Override
    public String getSerializedObjectName() {
        return "fields";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    @Override
    public void load(ParsedNode parsedNode, ResourceAccessor resourceAccessor) throws ParsedNodeException {
        fields = parsedNode.getChildren(null, "field")
                .stream()
                .map(ParsedNode::getValue)
                .map(String.class::cast)
                .collect(toList());
    }
}

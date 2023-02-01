package liquibase.ext.change;

import static org.assertj.core.api.Assertions.assertThat;

import liquibase.ext.statement.CreateCollectionStatement;
import org.junit.jupiter.api.Test;

import liquibase.statement.SqlStatement;

public class CreateCollectionChangeTest extends CouchbaseChangeTestCase {

    private static final String collectionName = "travels";
    private CreateCollectionChange createCollectionChange;

    @Test
    void expects_confirmation_message_is_create_collection() {
        createCollectionChange = new CreateCollectionChange(collectionName);

        String confirmationMessage = createCollectionChange.getConfirmationMessage();

        assertThat(confirmationMessage).isEqualTo("%s has successfully created", collectionName);
    }

    @Test
    void generate_statement_returns_only_create_collection_statement() {
        createCollectionChange = new CreateCollectionChange(collectionName);

        SqlStatement[] sqlStatements = createCollectionChange.generateStatements(database);

        assertThat(sqlStatements).containsExactly(new CreateCollectionStatement(collectionName));
    }

}
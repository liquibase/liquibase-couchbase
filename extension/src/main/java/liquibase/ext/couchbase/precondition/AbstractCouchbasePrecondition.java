package liquibase.ext.couchbase.precondition;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.visitor.ChangeExecListener;
import liquibase.database.Database;
import liquibase.exception.PreconditionErrorException;
import liquibase.exception.PreconditionFailedException;
import liquibase.exception.ValidationErrors;
import liquibase.exception.Warnings;
import liquibase.precondition.AbstractPrecondition;
import lombok.Data;

@Data
public abstract class AbstractCouchbasePrecondition extends AbstractPrecondition {

    @Override
    public Warnings warn(final Database database) {
        return new Warnings();
    }

    @Override
    public ValidationErrors validate(final Database database) {
        return new ValidationErrors();
    }

    @Override
    public void check(final Database database, final DatabaseChangeLog changeLog, final ChangeSet changeSet,
                      final ChangeExecListener changeExecListener)
            throws PreconditionFailedException, PreconditionErrorException {

        try {
            executeAndCheckStatement(database, changeLog);
        } catch (final PreconditionFailedException e) {
            throw e;
        } catch (final Exception e) {
            throw new PreconditionErrorException(e, changeLog, this);
        }
    }

    protected abstract void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws PreconditionFailedException;

    @Override
    public String getSerializedObjectNamespace() {
        return GENERIC_CHANGELOG_EXTENSION_NAMESPACE;
    }
}

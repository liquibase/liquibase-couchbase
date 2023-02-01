package liquibase.ext.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import liquibase.Scope;
import liquibase.exception.DatabaseException;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Properties;
import java.util.logging.Logger;

public class CouchbaseClientDriver implements Driver {

    @Override
    public Connection connect(final String url, final Properties info) {
        //Not applicable for non JDBC DBs
        throw new UnsupportedOperationException("Cannot initiate a SQL Connection for a NoSql DB");
    }

    public Cluster connect(final String connectionString, final ClusterOptions clusterOptions) throws DatabaseException {

        try {
            return Cluster.connect(connectionString, clusterOptions);
        } catch (final Exception e) {
            throw new DatabaseException("Connection could not be established to: "
                    + connectionString, e);
        }
    }

    @Override
    public boolean acceptsURL(final String url) {
        final String trimmedUrl = StringUtils.trimToEmpty(url);
        return trimmedUrl.startsWith(CouchbaseConnection.COUCHBASE_PREFIX) ||
                trimmedUrl.startsWith(CouchbaseConnection.COUCHBASE_SSL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        return (Logger) Scope.getCurrentScope().getLog(getClass());
    }
}

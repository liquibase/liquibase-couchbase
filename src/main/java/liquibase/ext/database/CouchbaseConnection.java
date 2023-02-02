package liquibase.ext.database;

/*-
 * #%L
 * Liquibase Couchbase Extension
 * %%
 * Copyright (C) 2023 Weigandt Consulting GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static liquibase.ext.database.Constants.BUCKET_PARAM;
import static liquibase.ext.database.Constants.COUCHBASE_PRIORITY;
import static liquibase.ext.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.database.Constants.COUCHBASE_PRODUCT_SHORT_NAME;

import com.couchbase.client.core.util.ConnectionString;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.sql.Driver;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import liquibase.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CouchbaseConnection implements DatabaseConnection {

    private ConnectionString connectionString;
    protected Cluster cluster;
    protected Bucket database;

    @Override
    public boolean supports(String url) {
        return Optional.ofNullable(url)
                .map(String::toLowerCase)
                .map(x -> x.startsWith(COUCHBASE_PRODUCT_SHORT_NAME))
                .orElse(false);
    }

    @Override
    public String getCatalog() throws DatabaseException {
        try {
            return database.name();
        } catch (final Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public String nativeSQL(String s) {
        return null;
    }

    @Override
    public void rollback() {
        throw new NotImplementedException();
    }

    @Override
    public void setAutoCommit(boolean b) {
        throw new NotImplementedException();
    }

    public String getDatabaseProductName() {
        return COUCHBASE_PRODUCT_NAME;
    }

    @Override
    public String getDatabaseProductVersion() {
        return "0";
    }

    @Override
    public int getDatabaseMajorVersion() {
        return 0;
    }

    @Override
    public int getDatabaseMinorVersion() {
        return 0;
    }

    @Override
    public String getURL() {
        return String.join(",", getHosts());
    }

    private List<String> getHosts() {
        return ofNullable(this.connectionString)
                .map(x -> x.hosts().stream()
                        .map(ConnectionString.UnresolvedSocket::host)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public String getConnectionUserName() {
        return ofNullable(this.connectionString).map(ConnectionString::username).orElse("");
    }

    @Override
    public boolean isClosed() {
        return isNull(cluster);
    }

    /**
     * Is not used in Couchbase
     */
    @Override
    public void attached(Database database) {
    }

    @Override
    public void open(final String url, final Driver driverObject, final Properties driverProperties)
            throws DatabaseException {

        try {
            this.connectionString = ConnectionString.create(StringUtils.trimToEmpty(url));

            Map<String, String> params = new HashMap<>();
            Optional.ofNullable(driverProperties)
                    .map(x -> x.get(BUCKET_PARAM))
                    .map(String.class::cast)
                    .ifPresent(val -> params.put(BUCKET_PARAM, val));

            this.connectionString.withParams(params);
            final String user = getAndTrimProperty(driverProperties, "user")
                    .orElseThrow(() -> new IllegalArgumentException("Username not specified in parameters"));
            final String password = getAndTrimProperty(driverProperties, "password").orElse(null);

            this.cluster = ((CouchbaseClientDriver) driverObject)
                    .connect(connectionString.original(), ClusterOptions.clusterOptions(user, password));

            if (this.connectionString.params().containsKey(BUCKET_PARAM)) {
                final String dbName = this.connectionString.params().get(BUCKET_PARAM);
                this.database = this.cluster.bucket(dbName);
            }
        } catch (final Exception e) {
            throw new DatabaseException("Could not open connection to database: " + getBucketName(url), e);
        }
    }

    private static Optional<String> getAndTrimProperty(Properties driverProperties, String user) {
        return Optional.ofNullable(driverProperties).map(props -> StringUtil.trimToNull(props.getProperty(user)));
    }

    private String getBucketName(String url) {
        return ofNullable(connectionString)
                .map(ConnectionString::params)
                .map(x -> x.get(BUCKET_PARAM))
                .map(String.class::cast)
                .orElse(url);
    }

    @Override
    public void close() throws DatabaseException {
        try {
            if (!isClosed()) {
                cluster.close();
                cluster = null;
            }
        } catch (final Exception e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void commit() {
        throw new NotImplementedException();
    }

    @Override
    public boolean getAutoCommit() {
        return false;
    }


    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT + COUCHBASE_PRIORITY;
    }
}

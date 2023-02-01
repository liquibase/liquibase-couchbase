package liquibase.ext.database;

/*-
 * #%L
 * Liquibase Couchbase Extension
 * %%
 * Copyright (C) 2023 Weigandt
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

import com.couchbase.client.core.util.ConnectionString;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import liquibase.util.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.sql.Driver;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static liquibase.ext.database.CouchbaseLiquibaseDatabase.COUCHBASE_PRODUCT_NAME;

@Getter
@Setter
@NoArgsConstructor
public class CouchbaseConnection implements DatabaseConnection {

    public static final int DEFAULT_PORT = 8091;
    public static final String COUCHBASE_PREFIX = COUCHBASE_PRODUCT_NAME + "://";
    public static final String COUCHBASE_SSL_PREFIX = COUCHBASE_PRODUCT_NAME + "s://";
    public static final String BUCKET_PARAM = "bucket";
    public static final int COUCHBASE_PRIORITY = 500;

    private ConnectionString connectionString;
    protected Cluster cluster;
    protected Bucket database;

    @Override
    public boolean supports(String url) {
        if (url == null) {
            return false;
        }
        return url.toLowerCase().startsWith("couchbase");
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
    public String nativeSQL(String s) throws DatabaseException {
        return null;
    }

    @Override
    public void rollback() throws DatabaseException {

    }

    @Override
    public void setAutoCommit(boolean b) throws DatabaseException {

    }

    public String getDatabaseProductName() throws DatabaseException {
        return COUCHBASE_PRODUCT_NAME;
    }

    @Override
    public String getDatabaseProductVersion() throws DatabaseException {
        return null;
    }

    @Override
    public int getDatabaseMajorVersion() throws DatabaseException {
        return 0;
    }

    @Override
    public int getDatabaseMinorVersion() throws DatabaseException {
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
    public boolean isClosed() throws DatabaseException {
        return isNull(cluster);
    }

    @Override
    public void attached(Database database) {

    }

    @Override
    public void open(final String url, final Driver driverObject, final Properties driverProperties)
            throws DatabaseException {

        try {
            this.connectionString = ConnectionString.create(StringUtils.trimToEmpty(url));

            Map<String, String> params = new HashMap<>();
            Optional.ofNullable(driverProperties).map(x -> x.get(BUCKET_PARAM))
                    .map(String.class::cast)
                    .ifPresent(val -> params.put(BUCKET_PARAM, val));

            this.connectionString.withParams(params);
            final String user = Optional.ofNullable(driverProperties).map(props -> StringUtil.trimToNull(props.getProperty("user"))).orElseThrow(() -> new IllegalArgumentException("Username not specified in parameters"));
            final String password = Optional.of(driverProperties).map(props -> StringUtil.trimToNull(props.getProperty("password"))).orElse(null);

            this.cluster = ((CouchbaseClientDriver) driverObject).connect(connectionString.original(), ClusterOptions.clusterOptions(user, password));

            if (this.connectionString.params().containsKey(BUCKET_PARAM)) {
                final String dbName = this.connectionString.params().get(BUCKET_PARAM);
                this.database = this.cluster.bucket(dbName);
            }
        } catch (final Exception e) {
            throw new DatabaseException("Could not open connection to database: " + getBucketName(url), e);
        }
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
    public void commit() throws DatabaseException {

    }

    @Override
    public boolean getAutoCommit() throws DatabaseException {
        return false;
    }


    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT + COUCHBASE_PRIORITY;
    }
}

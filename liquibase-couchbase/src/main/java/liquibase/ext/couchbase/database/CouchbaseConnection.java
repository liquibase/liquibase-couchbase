package liquibase.ext.couchbase.database;

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

import com.couchbase.client.core.util.ConnectionString;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import liquibase.ext.couchbase.executor.service.TransactionExecutorService;
import liquibase.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.sql.Driver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.couchbase.client.core.util.Validators.notNull;
import static com.couchbase.client.core.util.Validators.notNullOrEmpty;
import static com.couchbase.client.java.ClusterOptions.clusterOptions;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.database.Constants.BUCKET_PARAM;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRIORITY;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_SHORT_NAME;

/**
 * Main access point to the Couchbase database from Liquibase. Provides access to the {@link Cluster} and {@link Bucket} instances. The
 * latter is available only if the bucket name is specified in the connection URL.<br><br> Currently, doesn't support transactions and most
 * of ths SQL-specific things like {@link #attached(Database)} and {@link #nativeSQL(String)}.
 */

@Data
@NoArgsConstructor
public class CouchbaseConnection implements DatabaseConnection {

    private TransactionExecutorService transactionExecutorService;
    private ConnectionString connectionString;
    private Cluster cluster;
    private Bucket database;

    @Override
    public boolean supports(String url) {
        return ofNullable(url)
                .map(String::toLowerCase)
                .map(x -> x.startsWith(COUCHBASE_PRODUCT_SHORT_NAME))
                .orElse(false);
    }

    @Override
    public String getCatalog() throws DatabaseException {
        try {
            return ofNullable(database).map(Bucket::name)
                    .orElse(StringUtils.EMPTY);
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
        transactionExecutorService.clearStatementsQueue();
    }

    @Override
    public void setAutoCommit(boolean b) {
        // TODO investigate
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


    // TODO Still questionable , should we allow null connection string?
    private List<String> getHosts() {
        notNull(connectionString, "Connection string");
        return Optional.of(connectionString)
                .map(this::extractHosts)
                .orElse(emptyList());
    }

    @Override
    public String getConnectionUserName() {
        notNull(connectionString, "Connection string");
        notNullOrEmpty(connectionString.username(), "Username");
        return connectionString.username();
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
    public void open(final String url, final Driver driverObject, final Properties driverProperties) throws DatabaseException {

        try {
            String processedUrl = StringUtils.trimToEmpty(url);

            if (StringUtils.containsNone(processedUrl, '@')) {
                final String user = getAndTrimProperty(driverProperties, "user").orElseThrow(
                        () -> new IllegalArgumentException("Username not specified neither in parameters " + "nor in connection string"));
                String[] parts = processedUrl.split("://");
                processedUrl = parts[0] + "://" + user + '@' + parts[1];
            }
            Map<String, String> params = new HashMap<>();
            ofNullable(driverProperties).map(x -> x.get(BUCKET_PARAM))
                    .map(String.class::cast)
                    .ifPresent(val -> params.put(BUCKET_PARAM, val));
            connectionString = ConnectionString.create(processedUrl)
                    .withParams(params);

            final String password = getAndTrimProperty(driverProperties, "password").orElse(null);

            cluster = connect(connectionString.original(), clusterOptions(connectionString.username(), password));
            transactionExecutorService = TransactionExecutorService.getExecutor(cluster);

            if (connectionString.params().containsKey(BUCKET_PARAM)) {
                final String dbName = connectionString.params().get(BUCKET_PARAM);
                database = cluster.bucket(dbName);
            }
        } catch (final Exception e) {
            throw new DatabaseException("Could not open connection to database: " + getBucketName(url), e);
        }
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
        transactionExecutorService.executeStatementsInTransaction();
    }

    @Override
    public boolean getAutoCommit() {
        return false;
    }


    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT + COUCHBASE_PRIORITY;
    }

    private List<String> extractHosts(ConnectionString s) {
        return s.hosts()
                .stream()
                .map(ConnectionString.UnresolvedSocket::host)
                .collect(toList());
    }

    private Optional<String> getAndTrimProperty(Properties driverProperties, String user) {
        return ofNullable(driverProperties)
                .map(props -> props.getProperty(user))
                .map(StringUtil::trimToNull);
    }

    private String getBucketName(String url) {
        return ofNullable(connectionString).map(ConnectionString::params)
                .map(x -> x.get(BUCKET_PARAM))
                .map(String.class::cast)
                .orElse(url);
    }


    @SneakyThrows
    private Cluster connect(String connectionString, ClusterOptions clusterOptions) {
        try {
            return Cluster.connect(connectionString, clusterOptions);
        } catch (final Exception e) {
            throw new DatabaseException("Connection could not be established to: "
                    + connectionString, e);
        }
    }

}
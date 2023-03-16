package harness.compatibility.foundational

import groovy.transform.ToString
import groovy.transform.builder.Builder
import liquibase.Scope
import liquibase.database.Database
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase
import liquibase.harness.config.DatabaseUnderTest
import liquibase.harness.config.TestConfig
import liquibase.harness.util.FileUtils

class FoundationalTestDataProvider {

    final static String baseChangelogPath = "liquibase/harness/compatibility/foundational/changelogs/couchbase"
    final static List supportedChangeLogFormats = ['xml', 'json', 'yml', 'yaml'].asImmutable()

    static List<TestInput> buildTestInput(CouchbaseLiquibaseDatabase database) {
        String commandLineInputFormat = System.getProperty("inputFormat")
        if (commandLineInputFormat) {
            if (!supportedChangeLogFormats.contains(commandLineInputFormat)) {
                throw new IllegalArgumentException(commandLineInputFormat + " inputFormat is not supported")
            }
            TestConfig.instance.inputFormat = commandLineInputFormat
        }
        Scope.getCurrentScope().getUI().sendMessage("Only " + TestConfig.instance.inputFormat
                + " input files are taken into account for this test run")

        List<TestInput> inputList = new ArrayList<>()

        setTestContainerUrlToConfig(database)
        for (DatabaseUnderTest databaseUnderTest : TestConfig.instance.getFilteredDatabasesUnderTest()) {
            for (def changeLogEntry : FileUtils.resolveInputFilePaths(databaseUnderTest, baseChangelogPath, "xml").entrySet()) {
                inputList.add(TestInput.builder()
                        .databaseName(databaseUnderTest.name)
                        .url(databaseUnderTest.url)
                        .dbSchema(databaseUnderTest.dbSchema)
                        .username(databaseUnderTest.username)
                        .password(databaseUnderTest.password)
                        .version(databaseUnderTest.version)
                        .change(changeLogEntry.key)
                        .database(database)
                        .build())
            }
        }
        return inputList
    }

    private static void setTestContainerUrlToConfig(CouchbaseLiquibaseDatabase database) {
        def hostAndPort = database.connection.connectionString.hosts()
                .get(0)
                .getAt("hostAndPort")
        def connectionUrl = hostAndPort.getAt("formatted")
        def couchbaseConnection = "couchbase://$connectionUrl".toString()

        TestConfig.instance.getFilteredDatabasesUnderTest().stream()
                .filter { (it.name == "couchbase") }
                .forEach { it.url = couchbaseConnection }
    }

    @Builder
    @ToString(includeNames = true, includeFields = true, includePackage = false, excludes = 'database,password')
    static class TestInput {
        String databaseName
        String version
        String username
        String password
        String url
        String dbSchema
        String change
        Database database
    }
}

package harness.compatibility.foundational

import com.couchbase.client.java.json.JsonObject
import common.HarnessContainerizedSpecification
import common.operators.TestBucketOperator
import harness.compatibility.foundational.FoundationalTestDataProvider.TestInput
import liquibase.ext.couchbase.database.CouchbaseConnection
import liquibase.ext.couchbase.operator.ChangeLogOperator
import liquibase.harness.config.DatabaseUnderTest
import liquibase.harness.config.TestConfig
import liquibase.harness.util.TestUtils
import liquibase.harness.util.rollback.RollbackStrategy
import org.apache.commons.lang3.StringUtils
import spock.lang.Shared
import spock.lang.Unroll

import static common.HarnessTestConstants.*
import static liquibase.harness.util.FileUtils.getJSONFileContent

@Unroll
class HarnessCouchbaseCompatibility extends HarnessContainerizedSpecification {

    @Shared
    private RollbackStrategy strategy
    @Shared
    private List<DatabaseUnderTest> databases

    def setupSpec() {
        databases = TestConfig.instance.getFilteredDatabasesUnderTest()
        strategy = TestUtils.chooseRollbackStrategy()
        strategy.prepareForRollback(databases)
    }

    def "apply #testInput.change against #testInput.databaseName #testInput.version"() {
        given: "read input changeLog and expected data"
        def changelogs = getChangeLogs(testInput)
        def expectedResultSet = getJSONFileContent(testInput.change, testInput.databaseName, testInput.version, EXPECTED_FOLDER)
        def expectedJsonObject = JsonObject.fromJson(expectedResultSet)

        def argsMap = new HashMap<String, Object>()
        argsMap.put("url", testInput.url)
        argsMap.put("username", testInput.username)
        argsMap.put("password", testInput.password)

        and: "prepare operators"
        def changeLogOperator = new ChangeLogOperator(database)
        def bucketOperator = new TestBucketOperator(cluster.bucket(HARNESS_BUCKET))

        and: "create harness scope and default collection"
        bucketOperator.createScope(HARNESS_SCOPE)
        bucketOperator.createCollection(HARNESS_COLLECTION, HARNESS_SCOPE)

        and: "fail test if expectedResultSet is not provided"
        boolean expectedResultNotEmpty = StringUtils.isNotEmpty(expectedResultSet)
        assert expectedResultNotEmpty: "No expectedResultSet for ${testInput.change} against " +
                "${testInput.database.shortName} ${testInput.database.databaseMajorVersion}." +
                "${testInput.database.databaseMinorVersion}"

        and: "check database under test is online"
        def connection = testInput.database.connection
        def connectionIsOnline = connection instanceof CouchbaseConnection
        assert connectionIsOnline: "Database ${testInput.databaseName} ${testInput.version} is offline!"

        and: "execute Liquibase validate command to ensure a changelog is valid"
        for (int i = 0; i < changelogs.size(); i++) {
            argsMap.put("changeLogFile", changelogs.get(i))
            TestUtils.executeCommandScope("validate", argsMap)
        }

        when: "execute changelogs using liquibase update command"
        for (int i = 0; i < changelogs.size(); i++) {
            argsMap.put("changeLogFile", changelogs.get(i))
            TestUtils.executeCommandScope("update", argsMap)
        }

        and: "execute Liquibase tag command. Tagging last row of DATABASECHANGELOG collection"
        argsMap.remove("changeLogFile")
        argsMap.put("tag", "test_tag")
        TestUtils.executeCommandScope("tag", argsMap)

        and: "execute Liquibase history command"
        for (int i = 0; i < changelogs.size(); i++) {
            assert TestUtils.executeCommandScope("history", argsMap).toString().contains(changelogs.get(i))
        }

        and: "execute Liquibase status command"
        for (int i = 0; i < changelogs.size(); i++) {
            argsMap.put("changeLogFile", changelogs.get(i))
            assert TestUtils.executeCommandScope("status", argsMap).toString().contains("is up to date")
        }

        then: "obtain result set, compare it to expected result set"
        def lastRanChangeSetJsonObject = getLastRanChangesetHistory(changeLogOperator)
        def expectedHistoryJsonObject = expectedJsonObject.getObject("history")
        lastRanChangeSetJsonObject == expectedHistoryJsonObject

        and: "check for actual presence of created object"
        //TODO check if change only special types
        def numberOfDocuments = expectedJsonObject.getInt("numberOfDocuments")
        if (numberOfDocuments != null) {
            def numberOfResultDocuments = getResultDocuments()
            numberOfResultDocuments == numberOfDocuments
        }

        cleanup: "rollback changes if we ran changeSet"
        bucketOperator.dropScope(HARNESS_SCOPE)
        //TODO uncomment when rollback will be ready
//        if (shouldRunChangeSet) {
//            for (int i = 0; i < changelogList.size(); i++) {
//                argsMap.put("changeLogFile", changelogList.get(i))
//                strategy.performRollback(argsMap)
//            }
//        }

        where: "test input in next data table"
        testInput << FoundationalTestDataProvider.buildTestInput(database)
    }

    def cleanupSpec() {
        strategy.cleanupDatabase(databases)
    }

    private static List<String> getChangeLogs(TestInput testInput) {
        //in future also will be added sql, yml, json changesets
        return ["${CHANGELOGS_FOLDER}/${testInput.change}.xml"]
    }

    private static JsonObject getLastRanChangesetHistory(ChangeLogOperator changeLogOperator) {
        def historyChangeLogs = changeLogOperator.getAllChangeLogs()
        if (!historyChangeLogs.isEmpty()) {
            def lastChangeSet = historyChangeLogs[historyChangeLogs.size() - 1]
            return JsonObject.create()
                    .put("changeLog", lastChangeSet.changeLog)
                    .put("id", lastChangeSet.id)
                    .put("author", lastChangeSet.author)
                    .put("lastCheckSum", lastChangeSet.lastCheckSum.toString())
                    .put("description", lastChangeSet.description)
                    .put("tag", lastChangeSet.tag)
                    .put("execType", lastChangeSet.execType.name())
                    .put("orderExecuted", lastChangeSet.orderExecuted)
        }
        return null;
    }

    private static int getResultDocuments() {
        def selectNumberOfDocumentsQuery = "SELECT COUNT(*) as size FROM ${keyspace.getKeyspace()}"
        def rows = cluster.query(selectNumberOfDocumentsQuery).rowsAsObject()
        if (rows.isEmpty()) {
            throw new RuntimeException("Not documents in ${keyspace.getKeyspace()}")
        }
        return rows.get(0).getInt("size")
    }

}
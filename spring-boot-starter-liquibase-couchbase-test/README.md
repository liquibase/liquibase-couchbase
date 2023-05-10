# How to use spring-boot-test-project to run changesets


## Steps

1) In the root of **spring-boot-test-project** you can find `pom.xml` file where project settings are located. This project requires `spring-boot-starter-liquibase-couchbase` dependency. You need to set required dependency.
    ### If you are a user and want to test ready-made dependency:
   - Get desired version of dependency from ... and set it in `pom.xml`
    ### If you are a developer and want to test specific feature of dependency on some branch: 
   - At first, you need to choose branch from which you want to create dependency and put to local repository for test purpose. When you chose the branch create jar (dependency) of it <br/>
   To do this, **execute** `mvn clean install` command in the root of the **spring-boot-starter-liquibase-couchbase** module through CLI or using IDE.
   Make sure that in `pom.xml` of **spring-boot-test-project** the `spring-boot-starter-liquibase-couchbase` dependency has the correct version of our created dependency.
2) Change the properties (URL, username, password for the Couchbase database and path to changelog file) in the `src\main\resources\application.properties`
3) Write changelog files into the `src\main\resources\db\changelog` directory
4) Run CouchbaseLiquibaseStarterTest class

In **spring-boot-test-project** in the `src\main\resources` folder there is a `liquibase-couchbase.properties` file. In this file, there are properties specific for our Couchbase extension, such as transaction timeout, enable or disable reactive transactions, bucket for history data etc. (Full list you can see in main README -> Properties section [couchbase-liquibase](..)). <br/>

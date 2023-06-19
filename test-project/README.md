# How to use test-project to run changesets using maven pluign

The test-project module contains **Maven plugin** to run changelog files.

## Steps

1) In the root of **test-project** you can find `pom.xml` file where Maven plugin is located. This plugin requires an extension dependency. You need to set required dependency.
    ### If you are a user and want to test ready-made dependency:
   - Get desired version of extension from ... and set it in `pom.xml` of **test-project** to the `liquibase-maven-plugin`
    ### If you are a developer and want to test specific feature on some branch: 
   - At first, you need to choose branch from which you want to create dependency and put to local repository for test purpose. When you chose the branch create jar (dependency) of it <br/>
   To do this, **execute** `mvn clean install` command in the root of the **liquibase-couchbase** module through CLI or using IDE.
   Make sure that in `pom.xml` of **test-project** the `liquibase-maven-plugin` has the correct version of our created dependency.
2) Change the properties (URL, username, password for the Couchbase database) in the `src\main\resources\liquibase.properties`.
3) Write changelog files into the `src\main\resources\liquibase\changelog` directory.
4) Run the update command (apply all new changes). We can do this:
   - In the root of the **test-project** invoke the `mvn liquibase:update` command;
   - Using and IDE (Intellij IDEA for example) navigate to maven -> test-project -> plugins -> liquibase -> liquibase:update and invoke it.
5) In **test-project** in the `src\main\resources` folder there is a `liquibase-couchbase.properties` file. In this file, there are properties specific for our Couchbase extension, such as transaction timeout, enable or disable reactive transactions, bucket for history data etc. (Full list you can see in main README -> Properties section [couchbase-liquibase](..)). <br/>
To make these properties work, you also need to apply `mvn clean install` on **test-project** module. Because now these properties can only be read if we have jar file. <br/>
And every time when we change these properties we need to recreate jar so that the new values are read.

# How to use test-project to run changesets

The liquibase can be started using the following:
- Command-line interface (CLI)
- Maven/Gradle plugin
- Spring Boot application

The test-project module contains Maven plugin to run changelog files.

### Steps

1) In the root of **test-project** you can find `pom.xml` file where Maven plugin is located. This plugin requires an extension dependency. <br/>
Therefore, the first thing to do is create jar file of our extension and put it into the local repository. <br/>
To do this, **execute** `mvn clean install` command in the root of the **liquibase-couchbase** module through CLI or using IDE.
2) Change the properties (URL, username, password for the Couchbase database) in the `src\main\resources\liquibase.liquibase.properties` in the **test-project** module
3) Write changelog files into the `src\main\resources\liquibase\changelog` directory inside the **test-project** module.
4) Run the update command (apply all new changes). We can do this:
   - In the root of the **test-project** invoke the `mvn liquibase:update` command;
   - Using and IDE (Intellij IDEA for example) navigate to maven -> test-project -> plugins -> liquibase -> liquibase:update and invoke it.
5) In **test-project** in the `src\main\resources` folder there is a `liquibase-couchbase.properties` file. In this file, there are properties specific for our Couchbase extension, such as transaction timeout, enable or disable reactive transactions, bucket for history data etc. (Full list you can see in main README (TODO add link in future)). <br/>
To make these properties work, you also need to apply `mvn clean install` on **test-project** module. Because now these properties can only be read if we have jar file. <br/>
And every time when we change these properties we need to recreate jar so that the new values are read.

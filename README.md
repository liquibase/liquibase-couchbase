[![Java CI with Maven](https://github.com/wdt-dev/couchbase-liquibase/actions/workflows/actionsBuild.yml/badge.svg?branch=COS-260_github_actions)](https://github.com/wdt-dev/couchbase-liquibase/actions/workflows/actionsBuild.yml)

# Couchbase Extension for Liquibase

The Couchbase extension for Liquibase allows you to use migrate your database schema using Liquibase and store your changelogs in a
Couchbase bucket.

## Compatibility matrix

| Couchbase<br/> Version | Description | Comments     |
|------------------------|-------------|--------------|
| 7.1.3                  | Works       | Well tested  |
| 7.0.3                  | Works       | Stable       |
| < 7.0                  | Unsupported | Incompatible |

#### Notice

**Minimum supported version is 7.0** <br/>
As it uses latest Couchbase SDK, extension supports fully qualified keyspaces `Bucket.Scope.Collection ` <br/>
Which are incompatible with < **7.0** Cluster version <br />

#### Enterprise and Community versions

We support both Enterprise and Community couchbase versions. The only difference in `createBucket` changeType - in community version we shouldn't pass `compressionMode`, `conflictResolutionType`, or `maxExpiryInHours` options.

## About Liquibase

Liquibase is a tool that helps developers manage and track changes to a database's structure over time. It provides an easy way to version
database changes and apply them consistently across different environments. This can help ensure that database changes are made correctly
and reliably, without manual intervention.

Liquibase supports several changelog formats, including XML, YAML, JSON, and SQL. But **our extension supports** only XML (with XSD
validation) and JSON (without validation) for now.

Liquibase uses two tables to track database changes:

- The DATABASECHANGELOG table records details of each change that Liquibase has executed on the database, including the change set ID,
  unique ID, and other information about the change.

- The DATABASECHANGELOGLOCK table is used to prevent multiple instances of Liquibase from executing changes simultaneously on the same
  database. When Liquibase starts up, it tries to acquire a lock on this table.

In our case **we create collections** for these tables above and place them in special bucket for it. Default value of bucket location can
be changed, please see Properties section below for more information.

## Getting Started

This extension supports both XML (with schema validation) and JSON (without schema validation) changelogs.

Liquibase can be run in the next ways:

### Command-line interface (CLI)

- Install liquibase 4.21.1 version from https://github.com/liquibase/liquibase/releases/
- Download jar file from ... and put it into a `lib` directory in the Liquibase install location.
- Create folder and put the necessary files there:

```
  changeLog.xml
  liquibase.properties
  liquibase-couchbase.properties
```

- Example how to write changeLog.xml file you can find
  here [changeLogExample](test-project/src/main/resources/liquibase/changelog/demo.1-0.xml)
- liquibase.properties

```properties
url=<setUrlToDB>
username=<setUsername>
password=<setPassword>
changeLogFile=<PathToChangeLogFile>
driver=liquibase.ext.couchbase.database.CouchbaseStubDriver
```

- liquibase-couchbase.properties. This file is not required by default, but if you want to override default behaviour you need to create it
  and set properties.

```properties
serviceBucketName=<bucketName>
#...
#Full list of properties you can see below in the section Properties.
```

- Run liquibase commands. For example to apply all new changes execute `liquibase update --changelog-file=changeLog.xml`

### Maven/Gradle plugin

The guide how you can run liquibase using maven plugin you can see in the [test-project](test-project) directory. This directory contains
all necessary files and plugins to run.

### Spring Boot application

The guide how you can run liquibase using spring boot starter you can see in the [spring-boot-test-project](spring-boot-starter-liquibase-couchbase-test) directory. This project contains all necessary files to run.

**Spring boot version supports only liquibase update command.**

### Directly as a library jar file (create object from dependency and invoke commands)

Add extension dependency from ... and add to your project.

Now you can import Liquibase class and execute liquibase commands in your code. Here is the example of update command:

```java
    DatabaseFactory factory=DatabaseFactory.getInstance();
    ClassLoaderResourceAccessor resourceAccessor=new ClassLoaderResourceAccessor();
    Database db=factory.openDatabase("url","username","password",
            null,resourceAccessor);
    try(Liquibase liquibase=new Liquibase("config/liquibase/master.xml",resourceAccessor,db)) {
        liquibase.update();
    }
```

## ChangeType list
Change types:
- [Create bucket](test-project/src/main/resources/liquibase/changetypes/create-bucket.xml)
- [Update bucket](test-project/src/main/resources/liquibase/changetypes/update-bucket.xml)
- [Drop bucket](test-project/src/main/resources/liquibase/changetypes/drop-bucket.xml)
- [Create scope](test-project/src/main/resources/liquibase/changetypes/create-scope.xml)
- [Drop scope](test-project/src/main/resources/liquibase/changetypes/drop-scope.xml)
- [Create collection](test-project/src/main/resources/liquibase/changetypes/create-collection.xml)
- [Drop collection](test-project/src/main/resources/liquibase/changetypes/drop-collection.xml)
- Create index ([primary](test-project/src/main/resources/liquibase/changetypes/create-primary-index.xml)
  and [secondary](test-project/src/main/resources/liquibase/changetypes/create-secondary-index.xml))
- [Drop index](test-project/src/main/resources/liquibase/changetypes/drop-index.xml)
- [Insert document(s)](test-project/src/main/resources/liquibase/changetypes/insert-documents.xml) (either plain JSON inside the XML or JSON
  files)
- [Upsert document(s)](test-project/src/main/resources/liquibase/changetypes/upsert-documents.xml) (either plain JSON inside the XML or JSON
  files)
- [Mutate document](test-project/src/main/resources/liquibase/changetypes/mutate-in.xml)
- [Remove document(s)](test-project/src/main/resources/liquibase/changetypes/remove-documents.xml)
- [Execute query](test-project/src/main/resources/liquibase/changetypes/execute-query.xml)
- [Sql file](test-project/src/main/resources/liquibase/changetypes/sql-file.xml) (Only non-reactive)

Preconditions (About what preconditions is https://docs.liquibase.com/concepts/changelogs/preconditions.html):
- [Bucket exists](test-project/src/main/resources/liquibase/preconditions/bucket-exists-precondition.xml)
- [Scope exists](test-project/src/main/resources/liquibase/preconditions/scope-exists-precondition.xml)
- [Collection exists](test-project/src/main/resources/liquibase/preconditions/collection-exists-precondition.xml)
- Index exists ([primary](test-project/src/main/resources/liquibase/preconditions/primary-index-exists-precondition.xml) and [secondary](test-project/src/main/resources/liquibase/preconditions/secondary-index-exists-precondition.xml))
- [Document exists](test-project/src/main/resources/liquibase/preconditions/document-exists-precondition.xml)
- [Sql check](test-project/src/main/resources/liquibase/preconditions/sql-check-precondition.xml)

## Supported liquibase commands

- **update** - deploys any changes that are in the changelog file and that have not been deployed to your database yet.
- **status** - lists all undeployed changesets.
- **validate** - checks and identifies any possible errors in a changelog that may cause the update command to fail.
- **tag** - marks the current database state so you can roll back changes in the future.
- **rollback** - command rolls back changes made to the database based on the specified tag.
- **rollback-count** - sequentially reverts a specified number of changesets on your database.
- **rollback-to-date** - reverts your database to the state it was in at the date and time you specify.

## Properties

If you want override default couchbase properties, create `liquibase-couchbase.properties` and put in one of the two options:

- In the `resources` folder if you use **maven/gradle plugin** or **spring boot**;
- In the folder where you will invoke your **CLI** commands.

Available properties:

| Property name                          | Default value                   | Description                                                                                                         |
|----------------------------------------|---------------------------------|---------------------------------------------------------------------------------------------------------------------|
| lockservice.lockTtl                    | PT3M (Duration object format)   | Liquibase lock ttl                                                                                                  |
| lockservice.ttlProlongation            | PT1M (Duration object format)   | Time which will be added to prolong the lock ttl                                                                    |
| lockservice.changelogRecheckTime       | PT10S (Duration object format)  | Time to wait between rechecking the lock                                                                            |
| lockservice.changelogWaitTime          | PT300S (Duration object format) | Time to wait for the lock to be acquired                                                                            |
| lockservice.changelogCollectionName    | CHANGELOGLOCKS                  | Name of collection where lock documents will be created.                                                            | 
| serviceBucketName                      | liquibaseServiceBucket          | Name of bucket where history and lock collections will be created (DATABASECHANGELOG and CHANGELOGLOCKS)            |
| transaction.timeout                    | PT15S (Duration object format)  | Timeout of transaction                                                                                              | 
| transaction.reactive.enabled           | false                           | Flag to enable/disable executing operations on documents in reactive transaction                                    | 
| transaction.reactive.threads           | 16                              | Number of threads to execute operations reactively and parallel. <br/>Works only when reactive transactions enabled | 
| mutateIn.timeout                       | PT2S (Duration object format)   | Timeout of mutate in operation                                                                                      |

## Testing

Tests in the extension are written using JUnit 5 and Testcontainers. To run the tests, you will need to have Docker installed and running.
By default, integration and system tests are turned off. To run them, you will need to either set the `skipIntegrationTests` to `false` or
use the following command: `mvn clean install -DskipIntegrationTests=false`.

## Planned Features

- Support for data import from CSV files
- JSON schema validation

## Contributing

If you would like to contribute to the Couchbase extension for Liquibase, please see the [contribution guidelines](CONTRIBUTING.md).

## License

The Couchbase extension for Liquibase is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) and [NOTICE](NOTICE)
files for details.

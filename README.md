# Couchbase Extension for Liquibase

The Couchbase extension for Liquibase allows you to use migrate your database schema using Liquibase and store your changelogs in a Couchbase bucket.

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

## Planned Features
- Support for SQL++ queries
- Support for JSON as changelog format
- Support for CSV files
- Additional import modes for JSON files
- Reactive operations support
- Transaction configuration properties
- MutateIn configuration properties
- Dropping documents
- JSON schema validation

## Getting Started
An example of how you can use the Couchbase extension for Liquibase can be found in the [test-project](test-project) directory. 
It contains an example of how you can use the extension to create a bucket, create a scope, create a collection, create an index,
insert and upsert one or many documents.

This extension supports both XML (with schema validation) and JSON (without schema validation) changelogs.

## Installation
This extension could be used in one of two ways:
- Directly as a library jar file (main project in `liquibase-couchbase` directory) (see `LiquibaseSystemTest` as an example)
- As a Spring Boot starter jar (`spring-boot-starter-liquibase-couchbase` directory)

## Changelogs list
- Create bucket
- Update bucket
- Drop bucket
- Create scope
- Drop scope
- Create collection
- Drop collection
- Create index (primary and secondary)
- Drop index
- Insert document(s) (either plain JSON inside the XML or JSON files)
- Upsert document(s) (either plain JSON inside the XML or JSON files)
- Mutate document(s)

## Properties
If you want override default properties, create `liquibase-couchbase.properties` under your `resources` folder.
You can find list of supported properties in `CouchbaseLiquibaseConfiguration` class.

## Testing
Tests in the extension are written using JUnit 5 and Testcontainers. To run the tests, you will need to have Docker installed and running.
By default, integration and system tests are turned off. To run them, you will need to either set the `skipIntegrationTests` to `false` or 
use the following command: `mvn clean install -DskipIntegrationTests=false`.

## Contributing
If you would like to contribute to the Couchbase extension for Liquibase, please see the [contribution guidelines](CONTRIBUTING.md).

## License
The Couchbase extension for Liquibase is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) and [NOTICE](NOTICE) 
files for details.
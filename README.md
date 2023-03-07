# Couchbase Extension for Liquibase

The Couchbase extension for Liquibase allows you to use migrate your database schema using Liquibase and store your changelogs in a Couchbase bucket.

## Features
- Various operations support: inserting/upserting documents, creating/dropping indexes, creating/updating/dropping buckets, collections, scopes, etc.
- Sub-document operations support: adding/removing/updating fields and values in documents
- Using plain JSON inside XML changelogs

## Planned Features
- Support for N1QL queries
- Support for JSON as changelog format
- Support for CSV files
- Support for JSON files as data source with multiple import modes
- Reactive operations support
- Transaction configuration properties
- MutateIn configuration properties
- Dropping documents

## Getting Started
An example of how you can use the Couchbase extension for Liquibase can be found in the [test-project](test-project) directory. 
It contains an example of how you can use the extension to create a bucket, create a scope, create a collection, create an index,
insert and upsert one or many documents.

For more detailed information on how to use this extension, please refer to the documentation.

## Contributing
If you would like to contribute to the Couchbase extension for Liquibase, please see the [contribution guidelines](CONTRIBUTING.md).

## License
The Couchbase extension for Liquibase is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) and [NOTICE](NOTICE) files for details.
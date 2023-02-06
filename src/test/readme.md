# Test contribution

### General

Tests consist three parts
1. Given
2. When
3. Then

Generally we follow BDD guidelines, [see](https://www.baeldung.com/bdd-mockito) 

### Unit tests

Here we follow source package structure.<br />
Mock or make stub classes in order to verify only current class logic.

### Integration Tests

These tests might use containerized Couchbase.<br />
They test specific functionality part including several classes.<br />
**Important** - Every test class responsible to clean up resources of used container.

### Functional Tests

That kind of tests are launching entire flow of liquibase
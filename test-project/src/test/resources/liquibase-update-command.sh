mvn -f test-project/pom.xml clean package -Dmaven.test.skip
mvn -f test-project/pom.xml liquibase:update
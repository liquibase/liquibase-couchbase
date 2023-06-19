# Use an official Java runtime as the base image
FROM openjdk:8-jdk

# Build the application using Maven
RUN apt-get update && \
    apt-get install -y maven

CMD ["mvn", "-version"]
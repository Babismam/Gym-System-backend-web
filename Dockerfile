# ΣΤΑΔΙΟ 1: Build με Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM quay.io/wildfly/wildfly:30.0.0.Final-jdk17
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#123 --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]


COPY --from=build /app/target/gymmanagement.war /opt/jboss/wildfly/standalone/deployments/gymmanagement.war
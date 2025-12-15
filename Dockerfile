# ΣΤΑΔΙΟ 1: Build με Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Αντιγράφουμε πρώτα ΜΟΝΟ το pom.xml
COPY pom.xml .

# Κατεβάζουμε τα dependencies (ώστε να γίνουν cache)
RUN mvn dependency:go-offline

# Μετά αντιγράφουμε τον κώδικα
COPY src ./src

# Χτίζουμε το WAR
RUN mvn clean package -DskipTests

# ΣΤΑΔΙΟ 2: Wildfly Run
FROM quay.io/wildfly/wildfly:30.0.0.Final-jdk17
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#123 --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

COPY --from=build /app/target/gymmanagement.war /opt/jboss/wildfly/standalone/deployments/gymmanagement.war
# ΣΤΑΔΙΟ 1: Build με Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Χτίσιμο του WAR (παρακάμπτοντας τα tests για ταχύτητα)
RUN mvn clean package -DskipTests

# ΣΤΑΔΙΟ 2: Wildfly Run
FROM quay.io/wildfly/wildfly:30.0.0.Final-jdk17
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#123 --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

# Αντιγραφή και μετονομασία σε gymmanagement.war
# ΠΡΟΣΟΧΗ: Βεβαιώσου ότι στο pom.xml το <finalName> είναι gymmanagement
COPY --from=build /app/target/gymmanagement.war /opt/jboss/wildfly/standalone/deployments/gymmanagement.war
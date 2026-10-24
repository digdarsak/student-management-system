# Stage 1: build the app with Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# Stage 2: run it on a small Java image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Memory limit keeps it inside the 512 MB of a free server
ENTRYPOINT ["java", "-Xmx350m", "-Xss512k", "-jar", "app.jar"]

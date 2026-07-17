# Step 1: Maven build karega JAR file (Sahi Java 21 image ke sath)
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Java 21 runtime jo app chalayega
FROM eclipse-temurin:21-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
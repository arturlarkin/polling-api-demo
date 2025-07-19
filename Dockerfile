FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17

WORKDIR /app

COPY --from=build /app/target/polling-api-demo-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/polling-api-demo-0.0.1-SNAPSHOT.jar"]
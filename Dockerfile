#FROM openjdk:11-jdk-alpine as release
FROM openjdk:11.0.13-slim as release
WORKDIR /app
COPY target/redis-spring-jpa*.jar /app/app.jar
ENTRYPOINT ["java", "-jar"]
CMD ["/app/app.jar"]
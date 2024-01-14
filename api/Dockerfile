FROM eclipse-temurin:17-alpine
COPY target/api-monsters.jar /api-monsters.jar
# This is the port that your javalin application will listen on
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/api-monsters.jar"]
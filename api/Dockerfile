FROM eclipse-temurin:11-alpine
COPY target/apiTropBien.jar /app.jar
# This is the port that your javalin application will listen on
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/app.jar"]
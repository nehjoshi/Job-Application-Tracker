FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY target/tracker.jar tracker.jar
EXPOSE 8080
CMD ["java","-jar","tracker.jar"]
FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY target/tracker.jar tracker.jar
EXPOSE 5000
CMD ["java","-jar","tracker.jar"]
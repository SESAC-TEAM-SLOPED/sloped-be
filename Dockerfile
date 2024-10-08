FROM openjdk:17-alpine
WORKDIR /app
COPY ./build/libs/*.jar backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "backend.jar"]

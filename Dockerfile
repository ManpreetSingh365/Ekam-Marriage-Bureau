FROM openjdk:21
WORKDIR /app

COPY target/MarriageBureau-0.0.1-SNAPSHOT.jar /app/MarriageBureau-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "MarriageBureau-0.0.1-SNAPSHOT.jar"]
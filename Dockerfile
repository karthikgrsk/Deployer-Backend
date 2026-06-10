FROM eclipse-temurin:21

RUN apt-get update && \
    apt-get install -y nodejs npm git

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean install -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
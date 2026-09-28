FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN bash mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/module-02-final-project-1.0.jar"]

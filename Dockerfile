FROM maven:3.9.6-openjdk-21-slim

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

CMD ["mvn", "spring-boot:run"]

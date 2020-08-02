#FROM maven:3-jdk-8-slim AS compilation
#
#WORKDIR /compilation
#COPY pom.xml .
#RUN mvn dependency:go-offline
#COPY ./src .
#RUN mvn package
## compile assembly:single
#RUN ls -altrh
#RUN ls -altrh target
#RUN mv target/*.jar bundle.jar

FROM openjdk:8-alpine
WORKDIR /app
#COPY --from=compilation /compilation/bundle.jar /app
COPY ./target/EDI-T-0.0.1-SNAPSHOT.jar bundle.jar
COPY ./application.properties .

CMD ["java", "-jar", "bundle.jar"]

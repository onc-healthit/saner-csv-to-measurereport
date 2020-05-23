FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} saner-csv-adaptor.jar
ENTRYPOINT ["java","-jar","/saner-csv-adaptor.jar"]
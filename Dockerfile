FROM openjdk:17-jdk-slim

ADD /target/*.jar garage.jar

ENTRYPOINT ["java","-jar","/garage.jar"]

EXPOSE 3003
#BUILD APP
FROM maven:3.6.3-jdk-8 AS build
COPY src home/app/src
COPY pom.xml home/app
RUN mvn -f /home/app/pom.xml clean package

#PACKAGE
FROM openjdk:8-jre
COPY target/CountryCodeHelper.jar /CountryCodeHelper.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar", "/CountryCodeHelper.jar"]

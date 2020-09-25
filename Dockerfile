#BUILD
FROM maven:3.6.3-jdk-8 AS build
COPY src home/app/src
COPY pom.xml home/app
RUN mvn -f /home/add/pom.xml clean package

#PACKAGE
FROM openjdk:8-jre
COPY --from=build /home/app/tagret/CountryCodeHelper.jar /usr/local/lib/CountryCodeHelper.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar", "/usr/local/lib/CountryCodeHelper.jar"]

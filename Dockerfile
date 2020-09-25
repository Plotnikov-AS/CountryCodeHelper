FROM java:8
EXPOSE 8888
ADD /target/CountryCodeHelper.jar CountryCodeHelper.jar
ENTRYPOINT ["java","-jar", "CountryCodeHelper.jar"]

FROM openjdk:17-alpine
ADD target/backendDevTest-0.0.1-SNAPSHOT.jar /tmp/similar-products.jar
ENTRYPOINT ["java", "-jar", "/tmp/similar-products.jar"]
EXPOSE 5000

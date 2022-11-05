FROM openjdk:19
COPY ./hello-1.0-SNAPSHOT.jar /tmp
WORKDIR /tmp
CMD ["java","-jar","/tmp/hello-1.0-SNAPSHOT.jar"]
EXPOSE 443/tcp
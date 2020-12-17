FROM openjdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN mkdir /usr/local/mcc-api

ENTRYPOINT ["java","-jar","/app.jar"]

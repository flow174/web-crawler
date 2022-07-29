FROM adoptopenjdk/openjdk11:x86_64-ubuntu-jdk-11.28

MAINTAINER Beck.Zhang

ENV SERVICE_HOME=/home/beck

RUN mkdir -p ${SERVICE_HOME}

COPY ./target/*.jar ${SERVICE_HOME}/app.jar
COPY entrypoint.sh ${SERVICE_HOME}/

RUN chmod +x ${SERVICE_HOME}/entrypoint.sh

expose 8080

WORKDIR	${SERVICE_HOME}

ENTRYPOINT ["./entrypoint.sh"]

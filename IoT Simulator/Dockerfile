FROM openjdk:8-jre-alpine

WORKDIR /opt/app
ADD target/json-data-generator-bin.tar ./
WORKDIR /opt/app/json-data-generator

RUN touch conf/default_Simulator.json
RUN touch conf/default_Workflow.json


CMD ["java", "-jar", "json-data-generator.jar", "default_Simulator.json"]

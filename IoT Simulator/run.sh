#!/bin/sh

clean json-generator
docker run -d \
  --name json-generator
  docker.smartplatform.io/json-simulator:1.0.0


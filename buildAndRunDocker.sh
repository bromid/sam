#!/usr/bin/env sh
mvn clean package -T 1.5C || {
  echo 'Build failed';
  exit 1;
}

docker stop sam
docker rm sam

set -e
docker build -t atg/sam .
docker create -p 8080:8080 -p 27017:27017 --name sam atg/sam
docker start sam

source addTestData.sh

#!/usr/bin/env sh
mvn clean package -P local -pl \!atg-cmdb-release -T 1.5C || {
  echo 'Build failed';
  exit 1;
}

docker stop cmdb
docker rm cmdb

set -e
docker build -t atg/cmdb .
docker create -p 8080:8080 -p 27017:27017 --name cmdb atg/cmdb
docker start cmdb

source addTestData.sh

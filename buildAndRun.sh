#!/usr/bin/env sh
mvn clean package -P local -pl \!atg-cmdb-release -T 1.5C || {
  echo 'Build failed';
  exit 1;
}

java -jar atg-cmdb-server/target/atg-cmdb-server-1.0-SNAPSHOT.jar server atg-cmdb-server/default.yml

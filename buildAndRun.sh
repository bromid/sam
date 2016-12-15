#!/usr/bin/env sh
mvn clean package -T 1.5C || {
  echo 'Build failed';
  exit 1;
}

java -jar sam-server/target/sam-server-1.0-SNAPSHOT.jar server sam-server/default.yml

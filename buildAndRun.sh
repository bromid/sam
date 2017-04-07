#!/usr/bin/env sh

source ./common.sh

if [ "$SKIP_BUILD" = false ]; then
  mvn clean package -T 1.5C || {
    echo 'Build failed';
    exit 1;
  }
fi

java -jar sam-server/target/sam-server-1.0-SNAPSHOT.jar server sam-server/default.yml

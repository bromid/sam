#!/usr/bin/env sh

source ./common.sh

java -jar sam-server/target/sam-server-1.0-SNAPSHOT.jar dbtestdata sam-server/default.yml

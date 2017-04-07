#!/usr/bin/env sh

source ./common.sh

java -jar sam-integrationtest/target/sam-integrationtest-1.0-SNAPSHOT.jar test sam-integrationtest/default.yml

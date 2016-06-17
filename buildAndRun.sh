#!/bin/sh
mvn clean package -P local -pl \!atg-cmdb-release -T 1.5C
java -jar atg-cmdb-server/target/atg-cmdb-server-1.0-SNAPSHOT.jar server atg-cmdb-server/default.yml

#!/bin/sh
mvn clean package -P local
java -jar atg-cmdb-server/target/atg-cmdb-server-1.0-SNAPSHOT.jar server atg-cmdb-server/default.yml

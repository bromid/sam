# atg_cmdb
ATG Configuration Management DB for asset management.

### Getting started

1. Install latest version of Java SE 8 JDK http://www.oracle.com/technetwork/java/javase/downloads/index.html
2. Install latest version of Maven https://maven.apache.org/download.cgi
3. Install latest version of MongoDB https://www.mongodb.com/download-center?jmp=nav#community
4. Build and run ./buildAndRun.sh
5. *Optional* Reset the database with default test data ./addTestData.sh
6. *Optional* Reset the database and run integration tests ./runIntegrationTest.sh

### Change configuration

All configuration is found in [default.yml](atg-cmdb-server/default.yml) when run locally. When deployed with Ansible the configuration is a merge between [configuration/default.yml](atg-cmdb-release/src/ansible/configuration/default.yml) and [ansible/group_vars](atg-cmdb-release/src/ansible/group_vars).
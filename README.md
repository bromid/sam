# SAM - Simple Asset Management
[![Build status](https://travis-ci.org/atgse/sam.svg?branch=master)](https://travis-ci.org/atgse/sam)
[![License](https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000)](https://github.com/atgse/sam/blob/master/LICENSE.md)

SAM is a lightweight asset management system useful when a complete configuration management database ([CMDB](https://en.wikipedia.org/wiki/Configuration_management_database)) is not needed. It is developed for helping small organisations keeping control of the following four things:
* which applications exists in the organisation
* which groups are maintaining these applications
* which versions of these applications are deployed on which server
* which servers form a specific environment

SAM is assembled by three different components. A [MongoDB](https://www.mongodb.com/) database storing all information, a REST interface and a web GUI. Storing and maintaining the data in SAM can be done manually using the GUI but the preferred solution is using a collector which automatically collects information and updates SAM using the REST interface. The [SAM server collector](https://github.com/atgse/sam-collector) is one implementation.

### Glossary
#### Application
Any type of software deployed to a server.

#### Server
Any type of system which can host applications. Can be a virtual or physical server running any operating system or a clustered platform like [Kubernetes](https://kubernetes.io/). 

#### Group
Any type of organisational unit maintaining applications. A group can contain sub-groups forming a hierarchy.

#### Deployment
A specific version of an application deployed to a server.

#### Environment
A collection of servers. Common environments include test environments for functional or non-functional testing and production.

### Getting started
SAM is distributed as source code and needs to be built. The build can run on any platform supporting Java but the instructions below assumes a native or emulated bash environment. The instructions comes in two different flavors. One generating and running a docker container and one running MongoDB and a java application directly on the host.

#### Build and run with Docker
1. Install latest version of Java SE 8 JDK http://www.oracle.com/technetwork/java/javase/downloads/index.html
2. Install latest version of Maven https://maven.apache.org/download.cgi
3. Build and run ./buildAndRunDocker.sh
4. Surf to http://localhost:8080 in a browser
5. *Optional* Reset the database with default test data ./addTestData.sh
6. *Optional* Reset the database and run integration tests ./runIntegrationTest.sh

#### Build and run without Docker
1. Install latest version of Java SE 8 JDK http://www.oracle.com/technetwork/java/javase/downloads/index.html
2. Install latest version of Maven https://maven.apache.org/download.cgi
3. Install latest version of MongoDB https://www.mongodb.com/download-center?jmp=nav#community
4. Build and run ./buildAndRun.sh
5. Surf to http://localhost:8080 in a browser
6. *Optional* Reset the database with default test data ./addTestData.sh
7. *Optional* Reset the database and run integration tests ./runIntegrationTest.sh

### Configuration

All configuration is found in [default.yml](sam-server/default.yml).

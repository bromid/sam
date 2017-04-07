#
# SAM Dockerfile in CentOS 7 image
#

# Build:
# docker build -t atg/sam .
#
# Create:
# docker create -p 8080:8080 -p 27017:27017 --name sam atg/sam
#
# Start:
# docker start sam
#
# Connect with mongo
# docker exec -it sam mongo
#
# Connect bash
# docker exec -it sam bash


# Pull base image
FROM centos:centos7

RUN yum -y upgrade
RUN yum -y install wget && yum -y install gettext

# Install java from oracle website
ARG JAVA_VERSION=8u31
ARG BUILD_VERSION=b13

RUN wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-$BUILD_VERSION/jdk-$JAVA_VERSION-linux-x64.rpm" -O /tmp/jdk-8-linux-x64.rpm
RUN yum -y install /tmp/jdk-8-linux-x64.rpm

RUN alternatives --install /usr/bin/java jar /usr/java/latest/bin/java 200000
RUN alternatives --install /usr/bin/javaws javaws /usr/java/latest/bin/javaws 200000
RUN alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 200000

ENV JAVA_HOME /usr/java/latest

# Install MongoDB
RUN echo -e "[mongodb]\nname=MongoDB Repository\nbaseurl=https://repo.mongodb.org/yum/redhat/7/mongodb-org/3.2/`uname -m`/\ngpgcheck=0\nenabled=1" > /etc/yum.repos.d/mongodb.repo
RUN yum install -y mongodb-org
RUN yum clean all
RUN chown -R mongod:mongod /var/lib/mongo

# Copy config mongodb
ADD dockerconf/mongod.conf /etc/mongod.conf

# SAM jar file
ADD sam-server/target/sam-server-1.0-SNAPSHOT.jar /opt/sam.jar
ADD sam-server/default.yml /opt/default.yml

ARG OAUTH_CLIENT_ID
ARG OAUTH_CLIENT_SECRET
RUN envsubst < /opt/default.yml > /opt/default.yml

# Expose ports.
EXPOSE 8080
CMD /bin/mongod -f /etc/mongod.conf && java -jar /opt/sam.jar server /opt/default.yml

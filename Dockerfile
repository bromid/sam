#
# Cmdb Dockerfile in CentOS 7 image
#

# Build:
# docker build -t atg/cmdb .
#
# Create:
# docker create -it -p 27017:27017 --name cmdb atg/cmdb
#
# Start:
# docker start cmdb
#
# Connect with mongo
# docker exec -it cmdb mongo
#
# Connect bash
# docker exec -it cmdb bash


# Pull base image
FROM centos:centos7

#Install Java
ENV JAVA_VERSION 8u31
ENV BUILD_VERSION b13

RUN yum -y upgrade
RUN yum -y install wget
# Downloading Java
RUN wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/$JAVA_VERSION-$BUILD_VERSION/jdk-$JAVA_VERSION-linux-x64.rpm" -O /tmp/jdk-8-linux-x64.rpm

RUN yum -y install /tmp/jdk-8-linux-x64.rpm

RUN alternatives --install /usr/bin/java jar /usr/java/latest/bin/java 200000
RUN alternatives --install /usr/bin/javaws javaws /usr/java/latest/bin/javaws 200000
RUN alternatives --install /usr/bin/javac javac /usr/java/latest/bin/javac 200000

ENV JAVA_HOME /usr/java/latest
# Install MongoDB
RUN echo -e "[mongodb]\nname=MongoDB Repository\nbaseurl=https://repo.mongodb.org/yum/redhat/7/mongodb-org/3.2/`uname -m`/\ngpgcheck=0\nenabled=1" > /etc/yum.repos.d/mongodb.repo
#RUN yum update -y
RUN yum install -y mongodb-org
RUN yum clean all
RUN chown -R mongod:mongod /var/lib/mongo

# Copy config mongodb
ADD dockerstuff/mongod.conf /etc/mongod.conf

# Expose ports.
EXPOSE 27017
CMD /bin/mongod -f /etc/mongod.conf && tail -f /var/log/mongodb/mongod.log

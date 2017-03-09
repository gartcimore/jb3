FROM openjdk:8

# Update apt
RUN apt-get update

# Install mongodb
RUN apt-get install -y mongodb-server
RUN service mongodb start

# Install maven
RUN apt-get install -y maven

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN ["mvn", "package"]

EXPOSE 27017
EXPOSE 8080
ENTRYPOINT service mongodb start && java -jar target/jb3-1.0-SNAPSHOT.jar
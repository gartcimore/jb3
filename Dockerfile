FROM maven:3.6-jdk-11 as build
RUN mkdir /jb3
WORKDIR /jb3
ADD pom.xml pom.xml
ADD src src

# RUN mvn dependency:resolve && mvn verify && mvn package
RUN mvn dependency:resolve && mvn verify && mvn package

FROM adoptopenjdk/maven-openjdk11 as distribution

# Update apt
RUN apt-get update

# Install mongodb
RUN apt-get install -y mongodb-server
RUN service mongodb start

WORKDIR /code

COPY --from=build /jb3/* /code/

EXPOSE 27017
EXPOSE 8080
ENTRYPOINT service mongodb start && java -jar target/jb3-1.2-SNAPSHOT.jar

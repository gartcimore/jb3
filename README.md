# jb3

jb3 is a KISS tribune powered by java, spring boot and mongodb.

# Demo

A live demo is accessible [here](http://b3.bci.im).

You can also configure your prefered coincoin using these parameters:

- backend URL: http://b3.bci.im/legacy/xml
- post URL: http://b3.bci.im/legacy/post
- post data: message=%m
- tags: encoded

# How to use

## Requirements

- JDK 1.6+
- Maven 3+
- mongodb 2+

## Build

    mvn package

## Run locally

Ensure that mongodb is running and listening on 127.0.0.1 then run:

    java -jar target/jb3-*.jar

The frontend is now accessible on [locahost:8080](http://localhost:8080).

## Deploy and hosting on a production server

There is several options to deploy and host jb3. Here is one that requires:

- a domain name (example: mydomain.me).
- a web server with http proxy capabilities (example [Cherokee](http://cherokee-project.com/)).

### Application configuration

Edit the application.properties file and change the jb3.host property:

    jb3.host=http://mydomain.me

### Deployment

1. Build and launch jb3 on the server.
2. Configure your web server to act as reverse proxy on http://localhost:8080
# jb3

jb3 is a KISS tribune powered by java, spring boot and mongodb.

## Features

- archives
- fortunes
- bots
- last id
- XPost
- X-Post-Id
- rooms

# Demo

A live demo is accessible [here](http://b3.bci.im).

## Coincoin configuration

You can also configure your favorite coincoin using these parameters:

- backend URL: http://b3.bci.im/legacy/xml
- post URL: http://b3.bci.im/legacy/post
- post data: message=%m
- tags: encoded

## Advanced coincoin configuration

- backend using last id: http://b3.bci.im/legacy/xml?last=%last
- post returns backend: post request to http://b3.bci.im/legacy/post include backend data (same as GET http://b3.bci.im/legacy/xml response).
- post message using last id: message=%m&last=%last
- XPOST: post reply body contains last messages
- X-Post-Id: post reply returns posted message id in X-Post-Id header

## Rooms

Rooms are like IRC channel. From a coincoin, they can be seen as independent tribune.

- room backend : http://b3.bci.im/legacy/xml?room=%room
- post message in a room: message=%m&room=%room

## Gateway rooms

Gateway are used to receive and post on external tribunes or other chat systems.

Implemented gateways:

- euromussels
- sveetch

# How to use

## Skill check

Please note that a thorough knowledge of Java web application development and hosting is required.

## Requirements

- JDK 7+
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

### Security

In production environnement, please add

    jb3.secure=true

to your application.properties configuration. If you don't do it, jb3 will do
VERY INSECURE operations like:

- not verifying self signed HTTPS certificates.
- not verifying untrusted HTTPS certificates (like linuxfr.org CACERT certificate).

It is VERY RECOMMENDED, to use jb3.secure=true and add certificates to your
keystore. For example to add CACERT, use this guide:

http://wiki.cacert.org/FAQ/ImportRootCert?action=show&redirect=ImportRootCert#Java

### Deployment

1. Build and launch jb3 on the server.
2. Configure your web server to act as reverse proxy on http://localhost:8080

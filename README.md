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

## Run

Ensure that mongodb is running and listening on 127.0.0.1 then run:

    java -jar target/jb3-*.jar
Reactive REST Spring application for url shortening 
=========

Design choices
--------

This project is using Reactive Spring application using nanoid library to shorten url.

Building
--------

To build sources locally follow these instructions.

### Build and Run Unit Tests

Execute from project base directory:

    ./gradlew build bootJar

or use convenient script to do so

    ./build.sh

Running
--------
### Requirements

* docker 
* java 11

### How to start

To start application with default configuration execute

    ./start.sh

or run

    docker compose up -d
    java -jar build/libs/url-shortener-1.0.0-SNAPSHOT.jar

### Example curl API queries

Create short url for http://youtube.com

    curl -X POST -H "Content-Type: application/json" http://localhost:8080/short-url --data '{"url":"http://youtube.com"}'

Example response:

    {"key":"4NTn86XIjQ","originUrl":"http://youtube.com"}

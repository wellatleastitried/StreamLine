#!/bin/bash

cd ..
mvn clean install
java -jar target/streamline-0.1.0.jar

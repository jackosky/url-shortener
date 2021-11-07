#!/bin/sh

SCRIPT_PATH="`dirname \"$0\"`"
if  [ ! -e "$SCRIPT_PATH/build/libs/url-shortener-1.0.0-SNAPSHOT.jar" ]
then
   echo "Artifact not build, please execute build.sh first"
   exit 1
fi

docker compose up -d

java -jar build/libs/url-shortener-1.0.0-SNAPSHOT.jar "$@"

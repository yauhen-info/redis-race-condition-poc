#!/bin/sh
# the job shall spin up all the services excluding those used for testing
cd docker || exit
docker-compose build --parallel --remove-orphans
docker-compose up --scale rest-service=3 nginx

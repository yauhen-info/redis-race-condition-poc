#!/bin/bash
# the job shall spin up all the services excluding those used for testing
cd docker || exit
docker-compose build
docker-compose up --scale rest-service=3 nginx db_updater_service

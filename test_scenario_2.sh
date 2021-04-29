#!/bin/sh
cd docker || exit
docker-compose build --parallel
docker-compose up --scale rest-service=3 test_scenario_2

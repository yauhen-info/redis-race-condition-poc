#!/bin/sh
cd docker || exit
docker-compose up --scale rest-service=3 test_scenario_1

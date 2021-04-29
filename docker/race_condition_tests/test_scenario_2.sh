#!/bin/sh

MYSQL_DATABASE=score_db

EVENT_TO_TEST=game_1

prepare_mysql() {
  echo "Cleaning mysql up..."
  SQL_DELETE_ALL="DELETE from scores;"
  mysql ${MYSQL_DATABASE} -t -e "$SQL_DELETE_ALL"
  echo "Mysql cleaned up."
  echo "Adding an event to mysql..."
  SQL_INSERT_EVENT_TO_TEST="INSERT INTO scores values ('$EVENT_TO_TEST', 1);"
  mysql ${MYSQL_DATABASE} -t -e "$SQL_INSERT_EVENT_TO_TEST"
  echo "Added an event to mysql."
}
get_event_data_from_mysql() {
  SQL_SELECT_CMD="SELECT name, counter FROM scores WHERE name = '$EVENT_TO_TEST';"
  echo "Current value in MySql:"
  mysql ${MYSQL_DATABASE} -t -e "$SQL_SELECT_CMD"
}

update_event_data_from_mysql() {
  echo "Setting new value to MySql."
  SQL_UPDATE_CMD="UPDATE scores SET counter = counter+1 WHERE name = '$EVENT_TO_TEST';"
  mysql ${MYSQL_DATABASE} -e "$SQL_UPDATE_CMD"
  echo "New value has been set to MySql."
}
clean_up_redis() {
  # clean all redis keys
  echo "Cleaning redis up..."
  redis-cli -h redis -p 6379 FLUSHALL
  echo "Redis cleaned up."
}

get_event_data_from_redis() {
  echo "Current value in Redis:"
  redis-cli -h redis -p 6379 get $EVENT_TO_TEST
}

current_data_in_storages() {
  get_event_data_from_mysql
  get_event_data_from_redis
}

call_to_service_with_delay() {
  echo "Started a delayed request with the service_$1:"
  result=$(curl -s "docker_rest-service_$1:8080/$EVENT_TO_TEST?delay=$2")
  echo ""
  echo "Got result from rest-service-$1 (delayed)"
  echo "${result}"
  current_data_in_storages
}

sleeping() {
  echo ""
  echo "Holding on all commands for 3 sec"
  sleep 3
}
call_to_service_no_delay() {
  echo ""
  echo "Making request to service_$1 (no delay):"
  curl -s docker_rest-service_$1:8080/$EVENT_TO_TEST
  echo ""
}

echo "SCENARIO-2"

echo ""
prepare_mysql
clean_up_redis

current_data_in_storages
call_to_service_with_delay "1" "6000" &

sleeping 3
update_event_data_from_mysql
current_data_in_storages

call_to_service_no_delay "2"
current_data_in_storages

echo "Imitate expired key in redis"
clean_up_redis

sleep 10


#!/bin/bash
MYSQL_DATABASE=score_db

EVENT_TO_TEST=game_1

prepare_mysql() {
  echo "Cleaning MySql up..."
  SQL_DELETE_ALL="DELETE from scores;"
  mysql ${MYSQL_DATABASE} -t -e "$SQL_DELETE_ALL"
  echo "MySql cleaned up."
  echo "Adding an event to MySql..."
  SQL_INSERT_EVENT_TO_TEST="INSERT INTO scores values ('$EVENT_TO_TEST', 1);"
  mysql ${MYSQL_DATABASE} -t -e "$SQL_INSERT_EVENT_TO_TEST"
  echo "Added an event to MySql."
}
get_event_data_from_mysql() {
  SQL_SELECT_CMD="SELECT name, counter FROM scores WHERE name = '$EVENT_TO_TEST';"
  echo "Current value in MySql:" && mysql ${MYSQL_DATABASE} -t -e "$SQL_SELECT_CMD"
}

update_event_data_from_mysql() {
  echo "Setting new value to MySql..."
  SQL_UPDATE_CMD="UPDATE scores SET counter = counter+1 WHERE name = '$EVENT_TO_TEST';"
  mysql ${MYSQL_DATABASE} -e "$SQL_UPDATE_CMD" && echo "New value has been set to MySql."
}
clean_up_redis() {
  # clean all Redis keys
  echo "Cleaning Redis up..."
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
  echo "Holding on all commands for $1 sec"
  sleep $1
}
call_to_service_no_delay() {
  echo ""
  echo "Making request to service_$1 (no delay)..."
  curl -s docker_rest-service_$1:8080/$EVENT_TO_TEST
  echo ""
  echo "Finished request to service_$1 (no delay)."
}

LOGFILE="/path/to/log.log"
TIMESTAMP=`date "+%Y-%m-%d %H:%M:%S"`

echo "SCENARIO-3"
echo ""
prepare_mysql
clean_up_redis

current_data_in_storages
echo "Testing only for $EVENT_TO_TEST"

for i in {1..10}; do
  for j in 1 2; do
    $TIMESTAMP && curl -s docker_rest-service_${i}:8080/game_$j
    echo ""
  done
done

sleep 10

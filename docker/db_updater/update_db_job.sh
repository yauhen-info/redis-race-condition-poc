#!/bin/sh
while true; do
  mysql -p${MYSQL_ROOT_PASSWORD} ${MYSQL_DATABASE} -e "UPDATE scores SET counter = counter+1 WHERE name = 'game_1_player_1'"
  mysql -p${MYSQL_ROOT_PASSWORD} ${MYSQL_DATABASE} -e "select name, counter from scores WHERE name = 'game_1_player_1'"
  sleep 5
done

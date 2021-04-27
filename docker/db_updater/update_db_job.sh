#!/bin/sh
MYSQL_DATABASE=score_db
while true; do
  for j in 1 2 3 4 5; do
    SQL_UPDATE_CMD="UPDATE scores SET counter = counter+1 WHERE name = 'game_$j';"
    mysql ${MYSQL_DATABASE} -e "$SQL_UPDATE_CMD"

    SQL_SELECT_CMD="SELECT name, counter FROM scores WHERE name = 'game_$j';"
    mysql ${MYSQL_DATABASE} -t -e "$SQL_SELECT_CMD"
  done
  sleep 5
done

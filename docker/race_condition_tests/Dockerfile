FROM ubuntu:latest

RUN apt-get update; \
    apt-get install -y --no-install-recommends \
    mysql-client redis-tools curl

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_PWD=root
ENV MYSQL_DB=score_db
ENV MYSQL_DATABASE=score_db
ENV MYSQL_HOST=mysql-db
COPY ./my.cnf ~/.my.cnf

COPY test_scenario_1.sh /etc/test_scenario_1.sh
COPY test_scenario_2.sh /etc/test_scenario_2.sh
COPY test_scenario_3.sh /etc/test_scenario_3.sh
CMD ["chmod","+x","/etc/test_scenario_1.sh"]
CMD ["chmod","+x","/etc/test_scenario_2.sh"]
CMD ["chmod","+x","/etc/test_scenario_3.sh"]
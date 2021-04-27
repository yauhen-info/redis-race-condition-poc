FROM ubuntu:latest

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_PWD=root
ENV MYSQL_DB=score_db
ENV MYSQL_DATABASE=score_db
ENV MYSQL_HOST=mysql-db

RUN apt-get update; \
    apt-get install -y --no-install-recommends \
    mysql-client

COPY ./my.cnf ~/.my.cnf

COPY update_db_job.sh /etc/update_db_job.sh
CMD ["chmod","+x","/etc/update_db_job.sh"]
ENTRYPOINT ["/bin/sh", "/etc/update_db_job.sh"]

FROM ubuntu:latest
FROM mysql:8.0.24

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=score_db
EXPOSE 3306
COPY ./setup.sql /docker-entrypoint-initdb.d/

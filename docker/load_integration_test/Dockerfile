FROM alpine:latest

RUN apk add redis
COPY load_test_scenario_1.sh /tmp/load_test_scenario_1.sh
CMD ["chmod","0644","/tmp/load_test_scenario_1.sh"]
ENTRYPOINT ["/bin/sh", "/tmp/load_test_scenario_1.sh"]
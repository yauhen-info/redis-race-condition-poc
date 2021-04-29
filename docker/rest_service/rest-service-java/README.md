# redis-race-condition-poc

Proof-of-Concept (POC) project that demonstrates the feasibility of Redis as a shared application cache and will allows
to study the performance of the architecture

## REST endpoints

1. Health check - _host:port/ping_, e.g.

> curl localhost:8080/ping

shall return

> {"status": "OK"}

2. Data request endpoint
   _host:port/{key}?delay={number}_, e.g.

> curl localhost:8080/game_1?delay=3000

shall return

> {"key":"game_1", "value":"2", status: "OK", message:"Found"}

the parameter **delay** is optional and used only in race condition testing purposes, and if omitted - the default value is 0s.

## Database
- Mysql
- name: 'score_db'
- the only table: 'scores' (name, value)

```
Example
 +--------+---------+
 | name   | counter |
 +--------+---------+
 | game_1 |       1 |
 +--------+---------+
```

## What is this not about?

The POC is NOT for Production. Thus it was no necessary otherwise investment made into good Java development practices,
like

- code to interfaces;
- flexible and reasonable ORM;
- avoiding null values by being passed between a caller and a callee;
- unit tests coverage;
- big commits to VCS ('many' lines changed at once).

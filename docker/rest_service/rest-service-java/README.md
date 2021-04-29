# response-caching-redis-poc

Proof-of-Concept (POC) project that demonstrates the feasibility of Redis as a shared application cache and will allows
to study the performance of the architecture

## REST endpoints
1. Health check - host:port/ping, e.g. 
> curl localhost:8080/ping

shall return 

> {"status": "OK"}


## What is this not about?

The POC is NOT for Production. Thus it was no necessary otherwise investment made into good Java development practices,
like

- code to interfaces;
- flexible and reasonable ORM;
- avoiding null values by being passed between a caller and a callee;
- unit tests coverage;
- big commits to VCS ('many' lines changed at once).

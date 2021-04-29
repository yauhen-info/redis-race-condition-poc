## How to start the project?

While being under project root folder 'caching-redis-poc', run the following
> sh start_project.sh

## Architecture overview

![Services overview](pictures/redis-poc.png)

The previous command starts the services:

1. A single instance of **MySQL** with a database score_db, which consists of a single table 'scores' ;
2. A single instance of **DB updater job**, based on bash script. The job increments existing value for a row with
   name 'game_1_player_1' in 'scores' every 5 seconds. Once incremented, current value is printed;
3. Single instance of **Redis** with default settings;
4. Three instances of Java **rest-service**s. The number can be changed in ./start_project.sh
5. An **Nginx** service, with default configuration, running on port 4000 , which passes requests to the rest-service
   API endpoint.

## How to talk to services?

There is a single API endpoint to communicate with and exposed via HTTP:
> http://localhost:4000/{key},

e.g. from the command line
> curl http://localhost:4000/game_1_player_1

should respond with a JSON similar to

> {"key":"game_1_player_1", "value":"5", status: "OK", message:"Found"}

## What should Proof-of-Concept demonstrate?

The reference architecture attempts to solve the race condition scenario, i.e. avoid a case at which a client (at the
pic, at the right)
can get an invalid result from the cache. A result is considered invalid if for two sequential events E1 and E2 (like
scoring first and second goals in hockey), the client receives the event E2, and after E1.

An example scenario of a race condition:

|Time|Data Master|1st client (C1)|2nd client (C2)|
|---|---|---|---|
|1|Puts E1 into DB| | |
|2| |C1 reads event E1 from DB| |
|3|Puts E2 into DB| | |
|4| | |C2 reads event E2 from DB |
|5a| | |C2 puts event E2 into cache|
|5b| |  | C2 returns E2 as a response to HTTP end client|
|5c | |  | HTTP end client sees E2 from the cache |
|6a| | C1 puts event E1 into cache |  |
|6b | | C1 returns E1 as a response to HTTP end client| | 
|6c | | HTTP end client sees E1 from the cache, when calls next |  |

In the end, from the HTTP end client, the events are seen in the order E2, E1 (at time 5c and 6c accordingly).

But the order is the opposite to real world order, thus can mislead the end client, which we want to avoid.

## Testing the race conditions scenarios

There are three scenarios for testing race condition. All three implemented as a bash script calling different services
inside docker avoiding nginx, i.e. calling each service directly.

![Testing schema overview](pictures/testing-redis-poc.png)

### Scenario 1

Scenario tries to imitate a delay in one thread. It's tested with a single data entry named game_1 in MySQL

Can be started from the root folder with the command

> sh test_scenario_1.sh

|Time|Test job|1st client (C1)|2nd client (C2)|3rd client (C3)|MySQL state (event number)|Redis state (event number)|
|---|---|---|---|---|---|---|
|1a|Puts E1 into DB| | | | | |
|1b|Cleans up Redis| | | || |
|1c| | | | | 1| - |
|2a| | C1 reads the DB value E1 (in log 'Retrieved database value 1')| | || |
|2b| Noop for 1s to be sure E1 if read from DB | | || | |
|2c| | C1 sleeps for 6s in background (in log 'Waiting for 6000ms')| | || |
|3a|Updates DB with E2 instead of E1| | | || |
|3b| | | | |2| - |
|4a| | | C2 runs with no delay: reads E2 from DB (as redis is not filled yet by C1 with event E1) | || |
|4b| | | C2 sets E2 into Redis | || |
|4c| | | | | 2 | 2 |
|4d| | | C2 observes E2 on frontend, {"key":"game_1", "value":"2", status: "OK", message:"Found"} | || |
|5a| | | | C3 runs with no delay: reads E2 from DB (as redis is not filled yet by C1 with event E1) | || |
|5b| | | |  C3 sets E2 into Redis | || |
|5c| | | | | 2 | 2 |
|5d| | | | C3 observes E2 on frontend, {"key":"game_1", "value":"2", status: "OK", message:"Found"} | || |
|6| Noop for 5s in order to let C1 to get back form background with results | | || |
|7| | C1 observes E2 on frontend, {"key":"game_1", "value":"2", status: "OK", message:"Found"} | || |

### Scenario 2

The scenario is similar to the Scenario 1, but adds an extra step at the end emulating, that C1 has no key in Redis as
it expired already.

WIP

### Scenario 3

While data is updated MySql, thousands of requests are fired against each rest-service, which write the results into a
log file. The file is analyzed: it is being checked whether the cache had inconsistencies at any point in time, i.e.
earlier events appeared after later.

WIP

### Work to do

1. Grafana+prometheus for testing the performance
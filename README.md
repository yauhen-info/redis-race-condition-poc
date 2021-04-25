## How to start the project?

While being under project root folder 'caching-redis-poc', run the following
> sh start_project.sh

It will start the services:
1. A single instance of **MySQL** with a database score_db, which consists of a single table 'scores' ;
2. A single instance of **DB updater job**, based on bash script. 
   The job increments existing value for a row with name 'game_1_player_1' in 'scores' every 5 seconds. Once incremented, current value is printed;
3. Single instance of **Redis** with default settings;
4. Three instances of Java **rest-service**s. The number can be changed in ./start_project.sh
5. An **Nginx** service, with default configuration, running on port 4000 , which passes requests to the rest-service API endpoint. 

## How to talk to services?
There is a single API endpoint to communicate with and exposed via HTTP:
> http://localhost:4000/{key}, 

e.g. from the command line
> curl http://localhost:4000/game_1_player_1 

should respond with a JSON similar to

> {"data key":"game_1_player_1", "value":"1"}

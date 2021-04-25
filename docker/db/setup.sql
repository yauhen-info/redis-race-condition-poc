use score_db;
create table scores
(
    name    varchar(64),
    counter int(3)
);
insert into scores
values ('game_1_player_1', 1);
insert into scores
values ('game_1_player_2', 1);

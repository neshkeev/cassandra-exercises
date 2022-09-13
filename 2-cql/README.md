# Abstract

The goal of the exercise is to get familiar with write and read paths and compation strategies.

# Plan

## Preparation

1. Stop the previously started cluster if needed. Refer to the `1-initial-cassandra-setup` for instructions (`nodetool drain && nodetool stopdaemon`)
1. Remove the data directory: `rm -rf ./data`
1. Start a cassandra cluster: `docker compose up`
1. Wait for ~5 minutes for the cluster to start
1. Monitor containers: `watch docker ps --filter name=cass`
1. If any of the docker containers fail, restart it: `docker start cass1`
1. Enter the `cass1` docker container: `docker exec -it cass1 bash`
1. Examine the cluster's state: `nodetool status`

## Insertion

1. (Optional) Examine the cql script: `cat /mnt/scripts/setup_education_schema.cql`
1. Setup the `education` schema: `cqlsh --file="/mnt/scripts/setup_education_schema.cql"`
1. Enter a `cqlsh` session: `cqlsh`
1. Select the `education` keyspace: `use education`
1. Execute several queries:
    - `SELECT * FROM videos_by_title_year WHERE title = 'Introduction To Apache Cassandra' AND year = 2014;`
    - `SELECT * FROM videos_by_title_year WHERE title = 'Sleepy Grumpy Cat' AND year = 2015;`
1. Insert a row:
    - `INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id) VALUES ('Tips and Tricks with Apache Cassandra', 2022, '2022-08-13 04:05+0000', 'Learn Cassandra Tips and Tricks', 10d5c76c-8767-4db3-8050-e19e015b524c,2644c36e-14bd-11e5-839e-8438355b7e3a);`
1. Check the row:
    - `SELECT * FROM videos_by_title_year WHERE title = 'Tips and Tricks with Apache Cassandra' AND year = 2022;`
1. Execute an insert (notice there is a row with the primary key, hence UPSERT):
    - `INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id) VALUES ('Tips and Tricks with Apache Cassandra', 2022, '2021-02-03 15:15+0000', 'Learn Apache Cassandra Tips and Tricks with Professionals', 10d5c76c-8767-4db3-8050-e19e015b524c,2644c36e-14bd-11e5-839e-8438355b7e3a);`
1. Update a nonexisting row (UPSERT):
    - `UPDATE videos_by_title_year SET created_at = '2020-12-07 17:45+0000' WHERE title = 'Apache Cassandra for Experts' AND year = 2021;`
1. Check the row:
    - `SELECT * FROM videos_by_title_year WHERE title = 'Apache Cassandra for Experts' AND year = 2021;`
1. Lightweight Transactions (LWT):
    - `INSERT`: `INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id) VALUES ('Tips and Tricks with Apache Cassandra', 2022, '2021-02-03 15:15+0000', 'Learn Apache Cassandra Tips and Tricks with Professionals', 10d5c76c-8767-4db3-8050-e19e015b524c,2644c36e-14bd-11e5-839e-8438355b7e3a) IF NOT EXISTS;`
    - `UPDATE`: `UPDATE videos_by_title_year SET created_at = '2020-12-07 17:45+0000', description = 'Experts share thier experience with Apache Cassandra' WHERE title = 'Apache Cassandra for Experts' AND year = 2021 IF EXISTS;`

## TTL

TTL (Time to Live):
    - For the whole row: `INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id) VALUES ('Fun with Apache Cassandra', 2020, '2020-02-03 15:15+0000', 'Memes and jokes about Apache Cassandra', 10d5c76c-8767-4db3-8050-e19e015b524c, 2644c36e-14bd-11e5-839e-8438355b7e3a) USING TTL 10;`
    - `SELECT title, year, description, ttl(description) FROM videos_by_title_year WHERE title = 'Fun with Apache Cassandra' AND year = 2020;`
    - For a column: `UPDATE videos_by_title_year  USING TTL 10 SET description = 'This message will self destuct in 10 seconds' WHERE title = 'Apache Cassandra for Experts' AND year = 2021;`
    - `SELECT title, year, description, ttl(description) FROM videos_by_title_year WHERE title = 'Apache Cassandra for Experts' AND year = 2021;`
    - For a table:
        - `CREATE TABLE default_ttl(id TEXT PRIMARY KEY, name TEXT) WITH Default_time_to_live=10;`
        - `INSERT INTO default_ttl VALUES ('hello', 'world');`
        - `SELECT id, name, TTL(name) FROM default_ttl;`
    - Add `USING TTL 0` to remove TTL

## Delete

`DELETE`:
    - Delete a row: `DELETE FROM videos_by_title_year WHERE title = 'Introduction To Apache Cassandra' AND year = 2014;`
    - Delete a column: `DELETE description FROM videos_by_title_year WHERE title = 'Apache Cassandra for Experts' AND year = 2021;`
    - Delete all rows: `TRUNCATE default_ttl;`
    - Drop table: `DROP TABLE default_ttl;`

## Batch

### Unlogged Batch

1. Apply a batch:

```sql
BEGIN UNLOGGED BATCH

INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id)
VALUES ('Tips and Tricks with Apache Cassandra', 2019, '2022-08-13 04:05+0000', 'Learn Cassandra Tips and Tricks', 10d5c76c-8767-4db3-8050-e19e015b524c,2644c36e-14bd-11e5-839e-8438355b7e3a);

INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id)
VALUES ('Tips and Tricks with Apache Cassandra', 2022, '2021-02-03 15:15+0000', 'Learn Apache Cassandra Tips and Tricks with Professionals. Second Edition', 10d5c76c-8767-4db3-8050-e19e015b524c,2644c36e-14bd-11e5-839e-8438355b7e3a);

UPDATE videos_by_title_year
   SET created_at = '2020-12-07 17:45+0000'
 WHERE title = 'Tips and Tricks with Apache Cassandra'
  AND year = 2022;

APPLY BATCH;
```
1. Check data: `SELECT * FROM videos_by_title_year WHERE title = 'Tips and Tricks with Apache Cassandra' ALLOW FILTERING;`
1. Incorrect batch:

```sql
BEGIN UNLOGGED BATCH

INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id)
VALUES ('Apache Cassandra for Professionals', 2019, null, null, null, null);

INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id)
VALUES ('Tips and Tricks with Apache Cassandra', 2022, null, null, null, null);

UPDATE videos_by_title_year
   SET created_at = '2020-12-07 17:45+0000'
 WHERE title = 'Fun with Apache Cassandra'
  AND year = 2022;

APPLY BATCH;
```

### Logged Batch

1. Prepare data:

```sql
CREATE TABLE users (username text PRIMARY KEY, email TEXT, name TEXT);
CREATE TABLE users_by_email (email TEXT PRIMARY KEY, username TEXT);

INSERT INTO users (username, email, name) VALUES ('johndoe', 'johndoe@gmail.com', 'John');
INSERT INTO users_by_email (email, username) VALUES ('johndoe@gmail.com', 'johndoe');
```

1. Apply a batch:

```sql
BEGIN BATCH

UPDATE users SET email = 'john.doe@mail.com' WHERE username = 'johndoe';
UPDATE users_by_email SET email = 'john.doe@mail.com' WHERE email = 'johndoe@gmail.com';
DELETE FROM users_by_email WHERE email = 'johndoe@gmail.com'

APPLY BATCH;
```
1. Check data:

    - `SELECT * FROM users;`
    - `SELECT * FROM users_by_email;`

1. Incorrect batch:

```sql
BEGIN UNLOGGED BATCH

INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id)
VALUES ('Apache Cassandra for Professionals', 2019, null, null, null, null);

INSERT INTO videos_by_title_year (title, year, created_at, description, user_id, video_id)
VALUES ('Tips and Tricks with Apache Cassandra', 2022, null, null, null, null);

UPDATE videos_by_title_year
   SET created_at = '2020-12-07 17:45+0000'
 WHERE title = 'Fun with Apache Cassandra'
  AND year = 2022;

APPLY BATCH;
```

# Data types

## List

```sql
CREATE TABLE courses (id INT primary key, title TEXT, teachers LIST<TEXT>); 
INSERT INTO courses(id,title,teachers)     VALUES(1,'Cassandra',['me','you','he']);
SELECT * FROM courses;
UPDATE courses SET teachers=teachers+['her'] WHERE id=1;
UPDATE courses SET teachers=['boss']+teachers WHERE id=1;
UPDATE courses SET teachers=teachers-['her'] WHERE id=1;
DELETE teachers[2] FROM  courses WHERE id=1;
SELECT teachers FROM courses WHERE id=1;
```

## Set

```sql
CREATE TABLE likes (thing INT primary key, likers SET<INT>);
INSERT INTO likes(thing,likers) VALUES(1,{2,3,4});
UPDATE likes SET likers=likers+{5} WHERE thing=1;
UPDATE likes SET likers=likers-{5,2} WHERE thing=1;
SELECT * FROM likes;
```

## Map

```sql
CREATE TABLE contacts (id INT primary key, identities MAP<text,text>); 
INSERT INTO contacts(id,identities) VALUES(1,{'vk':'1234','fb':'4321','tw':'alex'});
UPDATE contacts SET identities['insta']='alex3' WHERE id=1;
UPDATE contacts SET identities['tw']='alex2' WHERE id=1;
UPDATE contacts SET identities['tw'] = null WHERE id=1;
DELETE identities['tw'] from contacts where id=1;
SELECT * FROM contacts;
```

SELECT dateof(now()) FROM system.local ;

DROP KEYSPACE python_keyspace;

CREATE KEYSPACE python_keyspace
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
   AND durable_writes = true;

CREATE TABLE python_keyspace.users_by_name (
  name text,
  dob timestamp,
  gender boolean,
  PRIMARY KEY ((name), dob, gender)
);
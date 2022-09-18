DROP KEYSPACE todokeyspace;

CREATE KEYSPACE todokeyspace
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
   AND durable_writes = true;

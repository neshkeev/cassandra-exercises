# Abstract

The goal of the exercise is to get familiar with `nodetool` - the main cassandra management tool.

# Plan

1. Start a cassandra cluster: `docker compose up`
1. Wait for 5 minutes for the cluster to start
1. Monitor containers: `watch docker ps --filter name=cass`
1. If any of the docker containers fail, restart it: `docker start cass1`
1. Enter the `cass1` docker container: `docker exec -it cass1 bash`
1. Get the node's info `nodetool info`
1. Check the status of the cluster: `nodetool status`
1. Get cluster info: `nodetool describecluster`
1. Stress test the cluster: `/opt/cassandra/tools/bin/cassandra-stress write n=50000 no-warmup -rate threads=1`
1. Examine the keyspace cassandra-stress created:
    - `cqlsh`
    - `use keyspace1;`
    - `DESCRIBE TABLES;`
    - `SELECT * FROM standard1 LIMIT 5;`
1. Shutdown the nodes:
    - Flush memtables: `nodetool drain`
    - Stop cassandra daemon: `nodetool stopdaemon`
    - Enter the other 2 tools and stop them too

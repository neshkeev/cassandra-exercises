# Abstract

The goal of the exercise is to get familiar managing cassandra's node management

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
1. Create schema: `cqlsh --file=/mnt/scripts/setup_schema.cql`
1. Exit the container: `exit`

## Adding a node

1. (A) Start a new cassandra node: `docker compose -f dc-node101.yaml up`
1. (B) In a new terminal enter the `cass1` docker container: `docker exec -it cass1 bash`
1. (B) Examine the cluster's status: `nodetool status`
1. (B) Check network stats: `nodetool netstats`
1. Wait till the third node joins the cluster
1. (C) In a new terminal pause `cass2`: `docker pause cass2`
1. (B) Start a `cqlsh` session: `cqlsh`
1. (B) Set the CL to `ALL`: `CONSISTENCY ALL;`
1. (B) Select data from table: `select * from education.videos_by_title_year;`
1. (C) Unpause `cass2`: `docker unpause cass2`
1. (B) Select data from table `select * from education.videos_by_title_year;`

## Removing a node

1. Enter the `cass2` container: `docker exec -it cass2 bash`
1. Flush memtables: `nodetool flush`
1. Synchronize data on the node: `nodetool repair`
1. Decommission the node: `nodetool decommission -f`
   **NOTICE** the `-f` option forces the node to get removed from the cluster, it's here only for demo purposes. Never do it in a production environment
1. Enter the `cass1` container: `docker exec -it cass1 bash`
1. Check the cluster's status: `nodetool status`
1. Start a `cqlsh` session: `cqlsh`
1. Set the CL to `ALL`: `CONSISTENCY ALL;`
1. Select data from table: `select * from education.videos_by_title_year;`

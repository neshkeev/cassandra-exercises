# Abtract

The goal of this exercise is to show how to use cassandra from a java project.

## Preparation

1. Stop the previously started cluster if needed. Refer to the `1-initial-cassandra-setup` for instructions (`nodetool drain && nodetool stopdaemon`)
1. Remove the data directory: `rm -rf ./data`
1. Start a cassandra cluster: `docker compose up`
1. Wait for ~5 minutes for the cluster to start
1. Monitor containers: `watch docker ps --filter name=cass`
1. If any of the docker containers fail, restart it: `docker start cass1`
1. Enter the `cass1` docker container: `docker exec -it cass1 bash`
1. Examine the cluster's state: `nodetool status`

## Setup the schema

1. Setup the `education` schema: `cqlsh --file=/mnt/scripts/setup_schema.cql`

## Run the app

1. Change the working directory: `cd cassandra-demo`
1. Run tests: `mvn test`

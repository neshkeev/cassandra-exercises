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

## Run the script

1. Assign to the `CONTACT_POINT` environment variable the IP address of the host where cassandra started
1. Assign to the `KEYSPACE_NAME` environment variable the name of the keyspace you created on the second step.
1. Export `CONTACT_POINT` and `KEYSPACE_NAME`
1. Create a virtual environment: `python -m venv .venv`
1. Activate the virtual environment: `source .venv/bin/activate`
1. Ensure the virtual environment is up: `which python`
1. Install packages: `pip install -r requirements.txt`
1. Run the script: `python demo.py`
1. Stop the virtual environment: `deactivate`
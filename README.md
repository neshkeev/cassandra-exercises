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

## Run the tests

1. Start a cqlsh session: `cqlsh`
1. Create a keyspace (the error in the keyspace is intentional): `CREATE KEYSPACE <KEYSPACE NAME HERE> WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'} AND durable_writes = true;`
1. Assign to the `CONTACT_POINT` environment variable the IP address of the host where cassandra started
1. Assign to the `KEYSPACE_NAME` environment variable the name of the keyspace you created on the second step.
1. Export `CONTACT_POINT` and `KEYSPACE_NAME`
1. In a new terminal jump into the `spring-boot-demo` directory: `cd spring-boot-demo`
1. Run tests: `mvn test`

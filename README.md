# Abstract

The goal of the exercise is to get familiar with replication and consistency levels.
Also shown how hinted hadoffs and read repairs assist in reaching a desired consistency state.

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

## Replicaiton

1. (Optional) Study the script: `cat /mnt/scripts/setup_education_schema.cql`
1. Setup the `education` schema: `cqlsh --file="/mnt/scripts/setup_education_schema.cql"`
1. Determine which nodes contain replicas:
    - Where `cassandra` partions reside: `nodetool getendpoints education videos_by_label 'cassandra'`
    - Where `advance` partions reside: `nodetool getendpoints education videos_by_label 'advance'`
1. Notice that cassandra doesn't maintain a key-value container with all the partitions, the actual nodes are calculated when requested.
  For example, execute `nodetool getendpoints education videos_by_label 'NONEXISTING'`

## Consistency Levels

1. Start a `cqlsh` session: `cqlsh`
1. Check current consistency level: `CONSISTENCY`
1. Set the consistency level to TWO: `CONSISTENCY TWO`
1. Select the `education` keyspace: `USE education;`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'cassandra';`
1. Turn off the `cass3` node:
    - Enter the `cass3` container: `docker exec -it cass3`
    - Drain memtables: `nodetool drain`
    - Stop cassandra daemon: `nodetool stopdaemon`
1. Back to `cass1`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'cassandra';`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`
1. Set the consistency level to `ONE`: `CONSISTENCY ONE`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'cassandra';`
1. Set the consistency level to `TWO`: `CONSISTENCY TWO`
1. Insert a new row into the `advance` partition: `INSERT INTO videos_by_label (label, created_at, video_id, title) VALUES ('advance', '2022-02-08', uuid(), 'Cassandra Drivers');`
1. Set the consistency level to `ONE`: `CONSISTENCY ONE`
1. Insert a new row into the `advance` partition: `INSERT INTO videos_by_label (label, created_at, video_id, title) VALUES ('advance', '2022-02-08', uuid(), 'Cassandra Drivers');`
1. Set the consistency level to `TWO`: `CONSISTENCY TWO`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`

## Hinted Handoffs

1. Find out where the `advance` replicas reside: `nodetool getendpoints education videos_by_label 'advance'`
1. Turn off both `cass2` and `cass3` nodes: `docker stop cass2 && docker stop cass3`
1. Enter the `cass1` container: `docker exec -it cass1`
1. Start a `cqlsh` session: `cqlsh`
1. Select the `education` keyspace: `USE education;`
1. Set the consistency level to `ANY` (__NOTICE__: Never (__!!!__) use `ANY` for production): `CONSISTENCY ANY`
1. Insert a new row into the `advance` partition: `INSERT INTO videos_by_label (label, created_at, video_id, title) VALUES ('advance', '2022-09-01', uuid(), 'Cassandra Performance Tunning');`
1. In a new terminal check the file system of `cass1` for storred hints: `ls -l /var/lib/cassandra/hints/`
1. Set the consistency level to `ONE`: `CONSISTENCY ONE`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'cassandra';`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`
1. In a new terminal start the `cass2` container: `docker start cass2`
1. In `cass1` execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`
1. In a new terminal check the file system of `cass1` for storred hints: `ls -l /var/lib/cassandra/hints/`
1. Start the `cass3` container: `docker start cass3`
1. Check the file system of `cass1` for storred hints: `ls -l /var/lib/cassandra/hints/`

# Read Repairs

1. Find out where the `advance` replicas reside: `nodetool getendpoints education videos_by_label 'advance'`
1. Turn off the `cass2` container:
    - Enter the cass2 container: `docker exec -it cass2 bash`
    - Drain memtables: `nodetool drain`
    - Stop cassandra daemon: `nodetool stopdaemon`
1. Examine the content of the `./data/cass2/data/education` directory: `ls ./data/cass2/data/education`
1. Remove the content of the `./data/cass2/data/education` directory: `rm -rfv ./data/cass2/data/education/*`
1. Set the consistency level to `ONE`: `CONSISTENCY ONE`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`
1. Start the `cass2` container: `docker start cass2`
1. Ensure the `cass2` is up: `nodetool status`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`
1. Set the consistency level to `TWO`: `CONSISTENCY TWO`
1. Execute the follwing query: `SELECT * FROM videos_by_label WHERE label = 'advance';`
1. Flush the memtables of `cass2` to disk:
    - Enter the cass2 container: `docker exec -it cass2 bash`
    - Flush memtables: `nodetool flush`
    - Exit the cass2 container: `exit`
1. Ensure the data of `cass2` is repaired: `ls ./data/cass2/data/education/videos_by_label-*/`

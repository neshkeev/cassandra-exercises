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

## Write Path

1. Examine the commitlog directory: `ls -lh /var/lib/cassandra/commitlog/`
1. Watch the commitlog directory for changes: `watch -n 1 -d "ls -lh /var/lib/cassandra/commitlog/"`
1. In a new terminal/ssh session enter the `cass1` docker container: `docker exec -it cass1 bash`
1. Start the `cassandra-stress` tool to write 250K records to the node:
  `/opt/cassandra/tools/bin/cassandra-stress write no-warmup n=250000 -port native=9042 -rate threads=1`
1. Switch to the terminal with the `watch` command. CTRL+C it when the `cassandra-stress` tool is done
1. Examine the `keyspace1.standard1` table's statistics: `nodetool cfstats keyspace1.standard1`
1. Review the "Memtable \*" statistics
1. Flush the memtable to disk: `nodetool flush`
1. Examine the `keyspace1.standard1` table's statistics: `nodetool cfstats keyspace1.standard1`. Notice that the "Memtable \*" statistics is empty
1. Look for commit logs reply operations: `grep 'CommitLog.java' /var/log/cassandra/system.log`
1. Restart the `cass1` docker container: `docker restart cass1`
1. Look for commit logs reply operations: `grep 'CommitLog.java' /var/log/cassandra/system.log`

## Read Path

1. Force cassandra flush memtables to the disk: `nodetool flush`
1. Examine the size of the bloom filter: `du -sh /var/lib/cassandra/data/keyspace1/standard1-*/*Filter.db`
1. Start a new `cqlsh` session: `cqlsh`
1. Check the bloom filter's configuration: `describe keyspace keyspace1`
1. Look for `bloom_filter_fp_chance` attribute
1. Reduce the probability for false positives in the bloom filter: `ALTER TABLE keyspace1.standard1 WITH bloom_filter_fp_chance = 0.0001;`
1. Verify the setting got changed: `describe keyspace keyspace1`
1. Update the SSTables: `nodetool upgradesstables --include-all-sstables`
1. Examine the size of the bloom filter file: `du -sh /var/lib/cassandra/data/keyspace1/standard1-*/*Filter.db`
1. Start a new `cqlsh` session: `cqlsh`
1. Remove the bloom filter: `ALTER TABLE keyspace1.standard1 WITH bloom_filter_fp_chance = 1.0;`
1. Update the SSTables: `nodetool upgradesstables --include-all-sstables`
1. Examine the bloom filter file: `ls -lh /var/lib/cassandra/data/keyspace1/standard1-*/*Filter.db`
1. Analyze the bloom filter statistics: `nodetool cfstats keyspace1.standard1`

## Compation Strategies

1. Setup the `education` schema: `cqlsh --file=/mnt/scripts/setup_education_schema.cql`
1. Start a `cqlsh` session: `cqlsh`
1. Select the `education` keyspace: `USE education`
1. Add a row: `INSERT INTO videos_by_label (label, created_at, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Cassandra Advanced');`
1. Open the second terminal/ssh session and flush memtables: `nodetool flush`
1. Check the data directory: `ls -lh /var/lib/cassandra/data/education/videos_by_label-*/`
1. Add a row: `INSERT INTO videos_by_label (label, created_at, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Cassandra Performance Tunning');`
1. In the second terminal/ssh session and flush memtables: `nodetool flush`
1. Check the data directory: `ls -lh /var/lib/cassandra/data/education/videos_by_label-*/`
1. Add a row: `INSERT INTO videos_by_label (label, created_at, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Cassandra Tips and Tricks');`
1. In the second terminal/ssh session and flush memtables: `nodetool flush`
1. Check the data directory: `ls -lh /var/lib/cassandra/data/education/videos_by_label-*/`
1. Add a row: `INSERT INTO videos_by_label (label, created_at, video_id, title) VALUES ('cassandra', dateof(now()), uuid(), 'Hitchhikers Guide to Cassandra');`
1. In the second terminal/ssh session and flush memtables: `nodetool flush`
1. Check the data directory: `ls -lh /var/lib/cassandra/data/education/videos_by_label-*/`

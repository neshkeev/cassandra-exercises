# Abstract

The goal of the exercise is to get familiar managing cassandra's nodes

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

## Snapshots

1. (A) Create a snapshot: `nodetool snapshot --table videos_by_title_year --tag 20220922-1 education`
1. (A) Start a new `cqlsh` session: `cqlsh`
1. (A) Remove all data from `education.videos_by_title_year`: `TRUNCATE education.videos_by_title_year;`
1. (A) Ensure that `education.videos_by_title_year` is empty: `select count(*) from education.videos_by_title_year;`
1. (B) Ensure that data files are gone: `ls /var/lib/cassandra/data/education/videos_by_title_year-*/`
1. (B) Copy the files from `snapshot` back to the `data` directory `education.videos_by_title_year`: `cp -v /var/lib/cassandra/data/education/videos_by_title_year-*/{snapshots/20220922-1/*,}`
1. (B) Restore data with `sstableloader`: `sstableloader -d $(hostname -i) /var/lib/cassandra/data/education/videos_by_title_year-*`
1. (B) Examine the `data`: `ls /var/lib/cassandra/data/education/videos_by_title_year-*/`
1. (A) Ensure the rows are back `education.videos_by_title_year`: `select count(*) from education.videos_by_title_year;`
1. (B) Remove the snapshot: `nodetool clearsnapshot --all`
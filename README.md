# Abstract

The goal of the exercise is to get familiar with cassandra's nodes and vnodes, ring, snitch configuration and gossip information.

# Plan

1. Stop the previously started cluster if needed. Refer to the `1-initial-cassandra-setup` for instructions (`nodetool drain && nodetool stopdaemon`)
1. Remove the data directory: `rm -rf ./data`
1. Start a cassandra cluster: `docker compose up`
1. Wait for ~5 minutes for the cluster to start
1. Monitor containers: `watch docker ps --filter name=cass`
1. If any of the docker containers fail, restart it: `docker start cass1`
1. Enter the `cass1` docker container: `docker exec -it cass1 bash`
1. Examine the cluster: `nodetool describecluster`. Notice:
    - Snitch: `org.apache.cassandra.locator.GossipingPropertyFileSnitch`
    - Data Centers: "Mars" and "Venus"
1. Get the cluster's information: `nodetool status`
1. Examine the ring: `nodetool ring`
1. Get gossip info: `nodetool gossipinfo`. Run few times, notice heartbeat changes
1. Restart `cass2` node: `docker restart cass2`
1. Go back to `cass1`: `docker exec -it cass1 bash`
1. Get gossip info: `nodetool gossipinfo`. Notice the heartbeat values
1. Get gossip info from `cqlsh`: `SELECT peer, data_center, host_id, preferred_ip, rack, release_version, rpc_address, schema_version FROM system.peers;`

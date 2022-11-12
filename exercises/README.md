# Exercies

## Write Path

1. What happens when a user executes an insert statement?
1. What parts of Cassandra participate in writes?
1. Do records appear on disk immediately?
1. What purpose do `Memtable`, `Commit Log Buffer`, `Commit Log` and `SSTable` serve?
1. How many memtables exist for a schema? Can there be many memtables?
1. How many `SSTable`s exist for a schema? Can there be many `SSTable` files for a table?
1. Ensure `cass1` node is up and running
1. Execute the `cassandra-stress` with 100K writes
1. Check the statistics for the `keyspace1.standard1` table
1. Flush memtable on disk
1. Ensure the `Memtable` is flushed on disk

## Read Path

1. What happens when a user queries rows from a table?
1. What is a Bloom Filter?
1. Make Bloom Filter yeild zero false positives. Don't forget to upgrade `SSTable`s.

## Compation Strategies

1. What purpose does compaction serve?
1. Trigger a compaction process
1. Ensure the compaction is finished

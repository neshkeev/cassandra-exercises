# Exercises

## Replicaiton

1. Determine which nodes store the partitions of the `videos_by_label` table which key is 'Interstellar'. Does this movie even exist in the table? Explain the result

## Consistency Levels

1. Ensure all three nodes are up and running
1. Turn off the `cass3` node
1. Start a new cqlsh session
1. Find out the current consistency level
1. Change the consistecy level to `ALL`. Explain what `ALL` means in this context
1. Get the total number of records from `education.videos_by_label`. Explain what happened
1. Adjust the consistecy level so it's possible to get the total number of records from `education.videos_by_label`
1. Start the `cass3` node

## Hinted Handoffs

1. Why you should never use the consistency level `ANY` in production?
1. Ensure all three nodes are up and running
1. Turn off `cass2` and `cass3`
1. Start a cqlsh session
1. Change the consistecy level to `ANY`
1. Insert a new row into `education.videos_by_label`. What happened? Why?
1. Ensure the `cass1` node stashed hints for other nodes. Why do we need hints?
1. Turn back on `cass2` and `cass3`
1. Do hints still exist? Why?

# Read Repairs

1. What is Read Repair?
1. When does Read Repair occur?
1. How are Read Repair and Hinted Handoffs related?
1. Ensure all three nodes are up and running
1. Stop `cass3`
1. Remove `cass3`'s data of the `education` schema from the filesystem
1. Set the consistency level to ALL
1. Get the total number of rows in the `education.videos_by_label`. What happened?
1. Turn the `cass3` back on
1. Get the total number of rows in the `education.videos_by_label` again
1. Examine the `cass3`'s data of the `education` schema on the filesystem. Don't forget to flush the memtables. How did the data get restored?

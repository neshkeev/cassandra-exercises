# Abstract

The goal of the exercise is to create a schema for hotels and reservations

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

## Setting up the hotel schema

1. (Optional) Examine the `hotel` schema: `cat /mnt/scripts/setup_hotel_schema.cql`
1. Setup the `reservation` schema: `cqlsh --file=/mnt/scripts/setup_hotel_schema.cql`
1. Notice the warning `Your replication factor 3 for keyspace hotel is higher than the number of nodes 2`
1. Start a new `cqlsh` session: `cqlsh`
1. Enter the `hotel` keyspace: `USE hotel;`
1. Examine the `hotels_by_id` table: `DESCRIBE hotels_by_id`
1. Insert a new hotel: `INSERT INTO hotel.hotels_by_id(hotel_id, name, phone, address) VALUES ( 6e60aeec-7c34-4753-ab88-94f64f50c000, 'Raddison', '+79123456789', { zip: '0000', country: 'Russia', city: 'Moscow', street: 'Sadovaya', building: '53 b12' });`
1. Select the city, street and building of the hotel: `SELECT address.city, address.street, address.city FROM hotel.hotels_by_id WHERE hotel_id = 6e60aeec-7c34-4753-ab88-94f64f50c000;`
1. Insert a new point of interest for the hotel: `INSERT INTO hotel.hotels_by_poi(poi_name, hotel_id, name, phone, address) VALUES ('Pushkin Statue', 6e60aeec-7c34-4753-ab88-94f64f50c000, 'Raddison', '+79123456789', { zip: '0000', country: 'Russia', city: 'Moscow', street: 'Sadovaya', building: '53 b12' });`
1. Check if there are hotels near `"Pushkin Statue"`: `select count(*) from hotel.hotels_by_poi where poi_name = 'Pushkin Statue'`

## Setting up the reservation schema

1. (Optional) Examine the `reservation` schema: `cat /mnt/scripts/setup_reservation_schema.cql`
1. Setup the `reservation` schema: `cqlsh --file=/mnt/scripts/setup_reservation_schema.cql`
1. Start a new `cqlsh` session: `cqlsh`
1. Enter the `reservation` keyspace: `USE reservation;`
1. Examine the tables: `DESCRIBE tables`

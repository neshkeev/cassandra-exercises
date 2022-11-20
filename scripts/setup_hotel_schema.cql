CREATE KEYSPACE hotel
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 3
};

CREATE TYPE hotel.address (
  zip text,
  country text,
  city text,
  street text,
  building text
);

CREATE TABLE hotel.hotels_by_id (
  hotel_id uuid,
  name text,
  phone text,
  address FROZEN<address>,
  PRIMARY KEY (hotel_id)
);

CREATE TABLE hotel.hotels_by_poi (
  poi_name text,
  name text,
  hotel_id uuid,
  phone text,
  address address,
  PRIMARY KEY ((poi_name), name, hotel_id)
);

CREATE TABLE hotel.available_rooms_by_hotel_date (
  hotel_id uuid,
  date date,
  room_number smallint,
  is_available boolean,
  PRIMARY KEY ((hotel_id), date, room_number)
);

CREATE TABLE hotel.pois_by_hotel (
  hotel_id uuid,
  poi_name text,
  description text,
  PRIMARY KEY ((hotel_id), poi_name)
);

CREATE TABLE hotel.amenities_by_room (
  hotel_id uuid,
  room_number smallint,
  amenity_name text,
  description text,
  PRIMARY KEY ((hotel_id, room_number), amenity_name)
);

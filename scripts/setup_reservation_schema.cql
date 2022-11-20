CREATE KEYSPACE reservation
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 3
};

CREATE TYPE reservation.address (
  zip text,
  country text,
  city text,
  street text,
  building text
);

CREATE TABLE reservation.reservations_by_hotel_date (
  hotel_id uuid,
  start_date date,
  room_number smallint,
  nights smallint,
  confirm_number text,
  guest_id uuid,
  PRIMARY KEY ((hotel_id, start_date), room_number)
);

CREATE TABLE reservation.reservations_by_confirmation (
  confirm_number text,
  hotel_id uuid,
  start_date date,
  room_number smallint,
  nights smallint,
  guest_id uuid,
  PRIMARY KEY ((confirm_number), hotel_id, start_date, room_number)
);

CREATE TABLE reservation.reservations_by_guest (
  guest_last_name text,
  hotel_id uuid,
  room_number smallint,
  start_date date,
  nights smallint,
  confirm_number text,
  guest_id uuid,
  PRIMARY KEY ((guest_last_name), hotel_id, room_number, start_date)
);

CREATE TABLE reservation.guests_by_id (
  guest_id uuid,
  first_name text,
  last_name text,
  title text,
  emails set<text>,
  phone_numbers list<text>,
  addresses map<text, frozen<address>>,
  PRIMARY KEY (guest_id)
);

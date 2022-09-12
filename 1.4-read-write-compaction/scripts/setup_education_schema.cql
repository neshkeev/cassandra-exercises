
CREATE KEYSPACE education
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

USE education;

CREATE TABLE videos_by_label (
  label text,
  video_id uuid,
  created_at timestamp,
  title text,
  PRIMARY KEY ((label), video_id)
);

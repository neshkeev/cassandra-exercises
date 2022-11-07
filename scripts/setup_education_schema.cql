
CREATE KEYSPACE education
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'SPb': 1,
  'Moscow': 1
};

USE education;

CREATE TABLE videos_by_label (
  label text,
  video_id uuid,
  created_at timestamp,
  title text,
  PRIMARY KEY ((label), created_at, video_id))
  WITH CLUSTERING ORDER BY (created_at DESC);

COPY videos_by_label(label, video_id, created_at, title)
FROM '/mnt/scripts/videos-by-label.csv'
WITH HEADER=TRUE;

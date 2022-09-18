
CREATE TABLE videos_by_title_year (
  title TEXT,
  year INT,
  created_at TIMESTAMP,
  description TEXT,
  user_id UUID,
  video_id TIMEUUID,
  PRIMARY KEY ((title, year))
);

COPY videos_by_title_year (title, year, created_at, description, user_id, video_id)
FROM '/mnt/scripts/videos_by_title_year.csv'
WITH HEADER=true;

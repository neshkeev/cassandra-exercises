import os
from datetime import datetime
from cassandra.cluster import Cluster
from cassandra import ConsistencyLevel
from cassandra.query import SimpleStatement


cluster = Cluster([ os.getenv("CONTACT_POINT") ], port = 19041)
session = cluster.connect(os.getenv("KEYSPACE_NAME"))

session.execute(
    """
    INSERT INTO users_by_name(name, dob, gender)
    VALUES (%s, %s, %s)
    """,
    ("John O'Reilly", datetime.now(), True)
)

session.execute(
    """
    INSERT INTO users_by_name(name, dob, gender)
    VALUES (%(name)s, %(dob)s, %(gender)s)
    """,
    {'name': "Jane", 'dob': datetime(2022, 8, 13, 10, 53, 10), 'gender': False}
)

query = SimpleStatement(
    "INSERT INTO users_by_name(name, dob, gender) VALUES (%s, %s, %s) IF NOT EXISTS",
    consistency_level=ConsistencyLevel.QUORUM)
session.execute(query, ('Sam', datetime(2007, 7, 6, 0, 14, 17), True))

rows = session.execute('SELECT name, dob, gender FROM users_by_name')
for user_row in rows:
    print(f"{user_row.name:20} | {user_row.dob:15} | {user_row.gender:3}")


def callback(rows):
    print("From callback")
    for i, row in enumerate(rows):
        print(f"{i}: {row.name}")

def log_error(exc):
    print("Operation failed: %s", exc)

future = session.execute_async('SELECT name, dob, gender FROM users_by_name')
future.add_callbacks(callback, log_error)
future.result()
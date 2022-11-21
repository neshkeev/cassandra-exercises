package com.neshkeev.cassandra.exercises;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableCommentByUser;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableCommentByVideo;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableUser;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableVideo;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableVideoViews;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createUdtVideoFormat;

public class Test2CreateSchemaTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test2CreateSchemaTest.class);

    @Test
    public void test() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            createUdtVideoFormat(cqlSession);
            createTableUser(cqlSession);
            createTableVideo(cqlSession);
            createTableVideoViews(cqlSession);
            createTableCommentByVideo(cqlSession);
            createTableCommentByUser(cqlSession);
            LOGGER.info("[OK] Success");
        }
    }
}

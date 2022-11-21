package com.neshkeev.cassandra.exercises;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test1ConnectivityTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test1ConnectivityTest.class);

    @Test
    public void test() {
        try(CqlSession ignore = CqlSession.builder().build()) {
            LOGGER.info("[SUCCESS]");
        }
    }
}
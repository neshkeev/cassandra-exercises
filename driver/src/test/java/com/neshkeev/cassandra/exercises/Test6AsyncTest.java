package com.neshkeev.cassandra.exercises;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.neshkeev.cassandra.exercises.schema.SchemaConstants.*;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableUser;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.truncateTable;

public class Test6AsyncTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test6AsyncTest.class);

    private static PreparedStatement stmtCreateUser;
    private static PreparedStatement stmtExistUser;

    @Test
    public void test() throws Exception {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            setup(cqlSession);

            String userEmail = "dwayne@sample.com";

            existUserAsync(cqlSession, userEmail)
                    .thenAccept(exist -> LOGGER.info("'{}' exists ? (expecting false): {}", userEmail, exist))
                    .thenCompose(r->createUserAsync(cqlSession, userEmail, "Dwayne", "Wade"))
                    .thenCompose(r->existUserAsync(cqlSession, userEmail))
                    .thenAccept(exist -> LOGGER.info("'{}' exists ? (expecting true): {}", userEmail, exist))
                    .toCompletableFuture()
                    .get();
        }
    }

    private static CompletionStage<Boolean> existUserAsync(CqlSession cqlSession, String email) {
        return cqlSession.executeAsync(stmtExistUser.bind(email))
                .thenApply(ars -> ars.one() != null);
    }

    @SuppressWarnings("SameParameterValue")
    private static CompletableFuture<Void> createUserAsync(CqlSession cqlSession, String email, String firstname, String lastname) {
        return cqlSession.executeAsync(stmtCreateUser.bind(email, firstname, lastname))
                .thenAccept(rs -> {
                    if (!rs.wasApplied()) {
                        throw new IllegalArgumentException("Email '" + email +
                                "' already exist in Database. Cannot create new user");
                    }
                    LOGGER.info("+ User {} has been created", email);
                }).toCompletableFuture();
    }

    private static void setup(CqlSession cqlSession) {
        createTableUser(cqlSession);

        truncateTable(cqlSession, USER_TABLENAME);

        stmtCreateUser = cqlSession.prepare(QueryBuilder.insertInto(USER_TABLENAME)
                .value(USER_EMAIL, QueryBuilder.bindMarker())
                .value(USER_FIRSTNAME, QueryBuilder.bindMarker())
                .value(USER_LASTNAME, QueryBuilder.bindMarker())
                .ifNotExists().build());
        stmtExistUser = cqlSession.prepare(QueryBuilder
                .selectFrom(USER_TABLENAME).column(USER_EMAIL)
                .whereColumn(USER_EMAIL)
                .isEqualTo(QueryBuilder.bindMarker())
                .build());
    }

}

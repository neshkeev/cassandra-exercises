package com.neshkeev.cassandra.exercises;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveResultSet;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.neshkeev.cassandra.exercises.dto.UserDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.neshkeev.cassandra.exercises.schema.SchemaConstants.*;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableUser;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.truncateTable;

public class Test7ReactiveTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test7ReactiveTest.class);

    private static PreparedStatement stmtUpsertUser;
    private static PreparedStatement stmtExistUser;
    private static PreparedStatement stmtFindUser;

    @Test
    public void test() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            setup(cqlSession);

            String userEmail  = "culkin@hotmail.com";

            existUserReactive(cqlSession, userEmail)
                    .doOnNext(exist -> LOGGER.info("'{}' exists ? (expecting false): {}", userEmail, exist))
                    .and(upsertUserReactive(cqlSession, userEmail, "Sergey", "Barn"))
                    .block();
        }
    }

    private static Mono<Boolean> existUserReactive(CqlSession cqlSession, String email) {
        ReactiveResultSet rrs = cqlSession.executeReactive(stmtExistUser.bind(email));
        return Mono.from(rrs)
                .map(rs -> true)
                .defaultIfEmpty(false);
    }

    private static Mono<Optional<UserDto>> findUserByIdReactive(CqlSession cqlSession, String email) {
        return Mono.from(cqlSession.executeReactive(stmtFindUser.bind(email)))
                .doOnNext(row -> LOGGER.info("+ Retrieved '{}': (expecting result) {}", row.getString(USER_EMAIL), email))
                .map(UserDto::new).map(Optional::of)
                .defaultIfEmpty(Optional.empty());
    }

    @SuppressWarnings("SameParameterValue")
    private static Mono<Void> upsertUserReactive(CqlSession cqlSession, String email, String firstname, String lastname) {
        ReactiveResultSet rrs = cqlSession.executeReactive(stmtUpsertUser.bind(email, firstname, lastname));
        return Mono.from(rrs).then();
    }

    private static void setup(CqlSession cqlSession) {
        // Create working table User (if needed)
        createTableUser(cqlSession);

        // Empty tables for tests
        truncateTable(cqlSession, USER_TABLENAME);

        stmtUpsertUser = cqlSession.prepare(QueryBuilder.insertInto(USER_TABLENAME)
                .value(USER_EMAIL, QueryBuilder.bindMarker())
                .value(USER_FIRSTNAME, QueryBuilder.bindMarker())
                .value(USER_LASTNAME, QueryBuilder.bindMarker())
                .build());
        stmtExistUser = cqlSession.prepare(QueryBuilder
                .selectFrom(USER_TABLENAME).column(USER_EMAIL)
                .whereColumn(USER_EMAIL)
                .isEqualTo(QueryBuilder.bindMarker())
                .build());
        PreparedStatement stmtDeleteUser = cqlSession.prepare(QueryBuilder
                .deleteFrom(USER_TABLENAME)
                .whereColumn(USER_EMAIL)
                .isEqualTo(QueryBuilder.bindMarker())
                .build());
        stmtFindUser = cqlSession.prepare(QueryBuilder
                .selectFrom(USER_TABLENAME).all()
                .whereColumn(USER_EMAIL)
                .isEqualTo(QueryBuilder.bindMarker())
                .build());

    }

}

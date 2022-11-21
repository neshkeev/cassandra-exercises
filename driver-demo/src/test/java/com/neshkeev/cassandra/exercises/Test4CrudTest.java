package com.neshkeev.cassandra.exercises;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.neshkeev.cassandra.exercises.dto.UserDto;
import org.junit.Test;

import static com.neshkeev.cassandra.exercises.schema.SchemaConstants.*;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableUser;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.truncateTable;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("SameParameterValue")
public class Test4CrudTest {

    private static PreparedStatement stmtCreateUser;
    private static PreparedStatement stmtUpsertUser;
    private static PreparedStatement stmtExistUser;
    private static PreparedStatement stmtDeleteUser;
    private static PreparedStatement stmtFindUser;

    @Test
    public void test() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            setup(cqlSession);
            String userEmail = "chen@sample.com";
            createUser(cqlSession, userEmail, "Fabian", "Crow");

            assertTrue("A user has just been created wasn't found", existUser(cqlSession, userEmail));

            String henryEmail = "henry@sample.com";
            updateUser(cqlSession, henryEmail, "Henry", "Ramirez");
            assertTrue("A user has just been created wasn't found", existUser(cqlSession, henryEmail));

            deleteUser(cqlSession, "NONEXISTING@mycorp.com");

            UserDto henry = findUserById(cqlSession, henryEmail);
            assertNotNull("A user should exist", henry);
        }
    }

    public void setup(CqlSession cqlSession) {
        createTableUser(cqlSession);
        truncateTable(cqlSession, USER_TABLENAME);

        stmtCreateUser = cqlSession.prepare(QueryBuilder.insertInto(USER_TABLENAME)
                .value(USER_EMAIL, QueryBuilder.bindMarker())
                .value(USER_FIRSTNAME, QueryBuilder.bindMarker())
                .value(USER_LASTNAME, QueryBuilder.bindMarker())
                .ifNotExists().build());

        // Using a - SLOW - lightweight transaction to check user existence
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
        stmtDeleteUser = cqlSession.prepare(QueryBuilder
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

    private static boolean existUser(CqlSession cqlSession, String email) {
        return cqlSession.execute(stmtExistUser.bind(email)).getAvailableWithoutFetching() > 0;
    }

    private static void createUser(CqlSession cqlSession, String email, String firstname, String lastname) {
        ResultSet rs = cqlSession.execute(stmtCreateUser.bind(email, firstname, lastname));
        if (!rs.wasApplied()) {
            throw new IllegalArgumentException("Email '" + email + "' already exist in Database. Cannot create new user");
        }
    }

    private static void updateUser(CqlSession cqlSession, String email, String firstname, String lastname) {
        cqlSession.execute(stmtUpsertUser.bind(email, firstname, lastname));
    }

    private static void deleteUser(CqlSession cqlSession, String email) {
        cqlSession.execute(stmtDeleteUser.bind(email));
    }

    private static UserDto findUserById(CqlSession cqlSession, String email) {
        ResultSet rs = cqlSession.execute(stmtFindUser.bind(email));
        Row record = rs.one();
        if (record == null) {
            return null;
        }
        return new UserDto(record);
    }
}

package com.neshkeev.cassandra.exercises;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.neshkeev.cassandra.exercises.schema.SchemaConstants.*;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.createTableUser;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.truncateTable;

public class Test8LightweightTransactionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test8LightweightTransactionTest.class);

    private static PreparedStatement stmtCreateUser;
    private static PreparedStatement stmtUpdateUserLwt;

    @Test
    public void test() {
        try (CqlSession cqlSession = CqlSession.builder().build()) {
            setup(cqlSession);

            String firstName = "Cedric";
            String lastName = "Diggory";
            String email = "jimmy@mail.com";

            boolean first  = createUserIfNotExist(cqlSession, email, firstName, lastName);
            boolean second = createUserIfNotExist(cqlSession, email, firstName, lastName);
            LOGGER.info("Created first time ? {} and second time {}", first, second);

            // Update if condition
            boolean applied1 = updateIf(cqlSession, email, firstName, "BEST");
            boolean applied2 = updateIf(cqlSession, email, firstName, lastName);
            LOGGER.info("Applied when correct value ? {} and invalid value {}", applied1, applied2);
        }
    }
    /**
     * The resultset is applied only if the record is created. If not the resultSet is populated
     * with existing data in DB (read)
     */
    private static boolean createUserIfNotExist(CqlSession cqlSession, String email, String firstname, String lastname) {
        return cqlSession.execute(stmtCreateUser.bind(email, firstname, lastname)).wasApplied();
    }

    /**
     * Note: we named the parameters as they are not in the same order in the query.
     */
    private static boolean updateIf(CqlSession cqlSession, String email, String expectedFirstName, String newLastName) {
        BoundStatement statement = stmtUpdateUserLwt.bind()
                .setString(USER_EMAIL, email)
                .setString(USER_FIRSTNAME, expectedFirstName)
                .setString(USER_LASTNAME, newLastName);
        return cqlSession.execute(statement).wasApplied();
    }

    private static void setup(CqlSession cqlSession) {
        createTableUser(cqlSession);
        truncateTable(cqlSession, USER_TABLENAME);

        /*
         * INSERT INTO users (email, firstname, lastname)
         * VALUES(?,?,?)
         * IF NOT EXISTS
         */
        SimpleStatement insert = QueryBuilder.insertInto(USER_TABLENAME)
                .value(USER_EMAIL, QueryBuilder.bindMarker())
                .value(USER_FIRSTNAME, QueryBuilder.bindMarker())
                .value(USER_LASTNAME, QueryBuilder.bindMarker())
                .ifNotExists()
                .build();
        stmtCreateUser = cqlSession.prepare(insert);

        /*
         * UPDATE users SET lastname=:lastname
         * WHERE email=:email
         * IF firstname=:firstname
         *
         * Operators available for LWT Condition:
         * =, <, <=, >, >=, != and IN
         */
        SimpleStatement update = QueryBuilder.update(USER_TABLENAME)
                .setColumn(USER_LASTNAME, QueryBuilder.bindMarker(USER_LASTNAME))
                .whereColumn(USER_EMAIL).isEqualTo(QueryBuilder.bindMarker(USER_EMAIL))
                .ifColumn(USER_FIRSTNAME).isEqualTo(QueryBuilder.bindMarker(USER_FIRSTNAME))
                .build();
        stmtUpdateUserLwt = cqlSession.prepare(update);
    }
}

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

public class Test3StatementsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Test3StatementsTest.class);
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Cena";

    @Test
    public void testStringStatement() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            cqlSession.execute(""
                    + "INSERT INTO users (email, firstname, lastname) "
                    + "VALUES ('clun@sample.com', 'Cedrick', 'Lunven')");

            LOGGER.info("[OK] Insert as a String");
        }
    }

    @Test
    public void testStatementPositional() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            SimpleStatement statement = SimpleStatement
                    .builder("INSERT INTO users (email, firstname, lastname) VALUES (?,?,?)")
                        .addPositionalValue("clun3@gmail.com")
                        .addPositionalValue(FIRST_NAME)
                        .addPositionalValue(LAST_NAME)
                    .build();

            cqlSession.execute(statement);
            LOGGER.info("[OK] Insert as a Positional Simple Statement");
        }
    }

    @Test
    public void testStatementNamed() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            SimpleStatement statement = SimpleStatement
                    .builder("INSERT INTO users (email, firstname, lastname) VALUES (:email, :firstName,:lastName)")
                        .addNamedValue("email", "clun5@gmail.com")
                        .addNamedValue("firstName", FIRST_NAME)
                        .addNamedValue("lastName", LAST_NAME)
                    .build();

            cqlSession.execute(statement);
            LOGGER.info("[OK] Insert as a Named Simple Statement");
        }
    }

    @Test
    public void testQueryBuilder() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            SimpleStatement statement = QueryBuilder
                    .insertInto(USER_TABLENAME)
                    .value(USER_EMAIL, QueryBuilder.literal("clun5@gmail.com"))
                    .value(USER_FIRSTNAME, QueryBuilder.literal(FIRST_NAME))
                    .value(USER_LASTNAME, QueryBuilder.literal(LAST_NAME))
                    .build();
            cqlSession.execute(statement);
        }
    }

    @Test
    public void testPreparedStatement() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            PreparedStatement ps = cqlSession.prepare("INSERT INTO users (email, firstname, lastname) VALUES (?,?,?)");
            BoundStatement bs1 = ps.bind("clun6@gmail.com", FIRST_NAME, LAST_NAME);
            cqlSession.execute(bs1);
        }
    }

    @Test
    public void testQueryBuilderPreparedStatement() {
        try (CqlSession cqlSession = CqlSession.builder().build()) {
            SimpleStatement statement = QueryBuilder
                    .insertInto(USER_TABLENAME)
                    .value(USER_EMAIL, QueryBuilder.bindMarker())
                    .value(USER_FIRSTNAME, QueryBuilder.bindMarker())
                    .value(USER_LASTNAME, QueryBuilder.bindMarker())
                    .build();

            PreparedStatement ps = cqlSession.prepare(statement);
            cqlSession.execute(ps.bind("clun7@gmail.com", FIRST_NAME, LAST_NAME));
            LOGGER.info("+ Insert with PrepareStatements + QueryBuilder");
        }
    }
}

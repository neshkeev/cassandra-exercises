package com.neshkeev.cassandra.exercises;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.neshkeev.cassandra.exercises.schema.SchemaConstants.*;
import static com.neshkeev.cassandra.exercises.schema.SchemaUtils.*;

public class Test5BatchTest {

    private static PreparedStatement insertIntoCommentByVideo;
    private static PreparedStatement insertIntoCommentByUser;
    private static PreparedStatement deleteCommentByVideo;
    private static PreparedStatement deleteCommentByUser;
    private static PreparedStatement selectCommentByVideo;
    private static PreparedStatement selectCommentByUser;

    @Test
    public void test() {
        try(CqlSession cqlSession = CqlSession.builder().build()) {
            setup(cqlSession);
            UUID user1 = UUID.randomUUID();
            UUID user2 = UUID.randomUUID();
            UUID videoId1 = UUID.randomUUID();
            UUID videoId2 = UUID.randomUUID();

            UUID commentId11 = createComment(cqlSession, user1, videoId1, "A comment from user1 for video1 which is good");
            UUID commentId21 = createComment(cqlSession, user2, videoId1, "A comment from user2 for video2 which is amazing");

            createComment(cqlSession, user1, videoId2, "Video2 is cool");
            createComment(cqlSession, user2, videoId2, "Video2 next level");

            retrieveCommentsVideo(cqlSession, videoId2).forEach(System.out::println);

            updateComment(cqlSession, commentId11, user1, videoId1, "This is user1's new comment");
            retrieveCommentsVideo(cqlSession, videoId1).forEach(System.out::println);

            deleteComment(cqlSession, commentId21, user2, videoId1);
            retrieveCommentsVideo(cqlSession, videoId1).forEach(System.out::println);

            retrieveCommentsUser(cqlSession, user1).forEach(System.out::println);
            retrieveCommentsVideo(cqlSession, videoId2).forEach(System.out::println);
        }
    }

    private static void setup(CqlSession cqlSession) {
        createTableCommentByUser(cqlSession);
        createTableCommentByVideo(cqlSession);

        // Comments are used in 2 queries, we need 2 tables to store it
        truncateTable(cqlSession, COMMENT_BY_USER_TABLENAME);
        truncateTable(cqlSession, COMMENT_BY_VIDEO_TABLENAME);

        insertIntoCommentByVideo = cqlSession.prepare(
                QueryBuilder.insertInto(COMMENT_BY_VIDEO_TABLENAME)
                        .value(COMMENT_BY_VIDEO_VIDEOID,   QueryBuilder.bindMarker())
                        .value(COMMENT_BY_VIDEO_USERID,    QueryBuilder.bindMarker())
                        .value(COMMENT_BY_VIDEO_COMMENTID, QueryBuilder.bindMarker())
                        .value(COMMENT_BY_VIDEO_COMMENT,   QueryBuilder.bindMarker())
                        .build());
        insertIntoCommentByUser = cqlSession.prepare(
                QueryBuilder.insertInto(COMMENT_BY_USER_TABLENAME)
                        .value(COMMENT_BY_USER_USERID,     QueryBuilder.bindMarker())
                        .value(COMMENT_BY_USER_VIDEOID,    QueryBuilder.bindMarker())
                        .value(COMMENT_BY_USER_COMMENTID,  QueryBuilder.bindMarker())
                        .value(COMMENT_BY_USER_COMMENT,    QueryBuilder.bindMarker())
                        .build());

        deleteCommentByUser = cqlSession.prepare(
                QueryBuilder.deleteFrom(COMMENT_BY_USER_TABLENAME)
                        .whereColumn(COMMENT_BY_USER_USERID).isEqualTo(QueryBuilder.bindMarker())
                        .whereColumn(COMMENT_BY_USER_COMMENTID).isEqualTo(QueryBuilder.bindMarker())
                        .build());
        deleteCommentByVideo = cqlSession.prepare(
                QueryBuilder.deleteFrom(COMMENT_BY_VIDEO_TABLENAME)
                        .whereColumn(COMMENT_BY_VIDEO_VIDEOID).isEqualTo(QueryBuilder.bindMarker())
                        .whereColumn(COMMENT_BY_VIDEO_COMMENTID).isEqualTo(QueryBuilder.bindMarker())
                        .build());

        selectCommentByVideo = cqlSession.prepare(
                QueryBuilder.selectFrom(COMMENT_BY_VIDEO_TABLENAME)
                        .column(COMMENT_BY_VIDEO_COMMENT)
                        .whereColumn(COMMENT_BY_VIDEO_VIDEOID).isEqualTo(QueryBuilder.bindMarker())
                        .build());

        selectCommentByUser  = cqlSession.prepare(
                QueryBuilder.selectFrom(COMMENT_BY_USER_TABLENAME)
                        .column(COMMENT_BY_USER_COMMENT)
                        .whereColumn(COMMENT_BY_USER_USERID).isEqualTo(QueryBuilder.bindMarker())
                        .build());
    }

    private static UUID createComment(CqlSession cqlSession, UUID userid, UUID videoId, String comment) {
        UUID commentId = Uuids.timeBased();
        updateComment(cqlSession, commentId, userid, videoId, comment);
        return commentId;
    }

    private static void updateComment(CqlSession cqlSession, UUID commentId, UUID userid, UUID videoId, String comment) {
        BatchStatement statement = BatchStatement
                .builder(BatchType.LOGGED)
                .addStatement(insertIntoCommentByVideo.bind(videoId, userid, commentId, comment))
                .addStatement(insertIntoCommentByUser.bind(userid, videoId, commentId, comment))
                .build();
        cqlSession.execute(statement);
    }

    private static void deleteComment(CqlSession cqlSession, UUID commentId, UUID userid, UUID videoId) {
        cqlSession.execute(BatchStatement
                .builder(BatchType.LOGGED)
                .addStatement(deleteCommentByUser.bind(userid, commentId))
                .addStatement(deleteCommentByVideo.bind(videoId, commentId))
                .build());
    }

    private static List<String> retrieveCommentsVideo(CqlSession cqlSession, UUID videoId) {
        return cqlSession.execute(selectCommentByVideo.bind(videoId))
                .all()
                .stream()
                .map(row -> row.getString(COMMENT_BY_VIDEO_COMMENT))
                .collect(Collectors.toList());
    }

    private static List<String> retrieveCommentsUser(CqlSession cqlSession, UUID userId) {
        BoundStatement statement = selectCommentByUser.bind(userId);
        return cqlSession.execute(statement)
                .all()
                .stream()
                .map(row -> row.getString(COMMENT_BY_USER_COMMENT))
                .collect(Collectors.toList());
    }
}

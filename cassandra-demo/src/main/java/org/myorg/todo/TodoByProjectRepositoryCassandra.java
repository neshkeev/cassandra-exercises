package org.myorg.todo;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Consistency;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoByProjectRepositoryCassandra extends CassandraRepository<TodoByProject, String> {
    @Consistency(DefaultConsistencyLevel.ONE)
    boolean existsByProject(@NonNull String s);

    @Consistency(DefaultConsistencyLevel.ALL)
    List<TodoByProject> findAllByProjectAndDueDateLessThan(String project, LocalDateTime dueDate);
}

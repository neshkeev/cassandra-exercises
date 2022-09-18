package org.myorg.todo;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Consistency;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodoByIdRepositoryCassandra extends CassandraRepository<TodoById, UUID> {

    @Consistency(DefaultConsistencyLevel.QUORUM)
    @NonNull Optional<TodoById> findByTodoId(@NonNull UUID uuid);
}

package org.myorg.todo;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BatchType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.InsertOptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@SpringBootTest
public class TodoByProjectSpringDataApplicationTests {

	@Autowired
	private TodoByProjectRepositoryCassandra todoByProjectRepo;

    @Autowired
    private TodoByIdRepositoryCassandra todoByIdRepo;

	@Autowired
	private CassandraOperations ops;

	@Test
	void testSaveTaskWhenSave() {
		String projectName = "Cassandra Demo";
		String description = "Description";

		List<Object> objects = getObjects(projectName);

		TodoById todoById = (TodoById) objects.get(0);
		todoById.setDescription(description);

		InsertOptions insertOptions = InsertOptions.builder()
				.consistencyLevel(ConsistencyLevel.QUORUM)
				.build();
		ops.batchOps(BatchType.LOGGED)
				.insert(objects, insertOptions)
				.execute();

	    Assertions.assertTrue(todoByProjectRepo.existsByProject(projectName));
		assertThat("Should contain a row in 'todo_by_project'", todoByProjectRepo.existsByProject(projectName), is(true));

		Optional<TodoById> maybeTodo = todoByIdRepo.findByTodoId(todoById.getTodoId());
		//noinspection OptionalGetWithoutIsPresent
		Assertions.assertEquals(maybeTodo.get().getDescription(), description);
		assertThat("Should have description", maybeTodo.get().getDescription(), is(description));

		List<TodoByProject> todos = todoByProjectRepo.findAllByProjectAndDueDateLessThan(projectName, LocalDateTime.now().plusDays(10));
		assertThat("Should contain todos", todos, is(not(empty())));
	}

	private static List<Object> getObjects(String projectName) {
		int daysToAdd = new Random().nextInt(10) - 5;
		int priority = new Random().nextInt(50);
		TodoByProject todoByProject = new TodoByProject(projectName,
				LocalDateTime.now().plusDays(daysToAdd),
				priority,
				UUID.randomUUID(),
				"Task " + new Random().nextInt(100),
				false);

		TodoById todoById = new TodoById(todoByProject);

		return List.of(todoById, todoByProject);
	}
}
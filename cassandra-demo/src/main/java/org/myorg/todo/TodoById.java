package org.myorg.todo;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(value = "todos_by_id")
public class TodoById {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 0, name = "todo_id")
    @CassandraType(type = CassandraType.Name.UUID)
    private UUID todoId;

    @Column("title")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String title;

    @Column("description")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String description;

    @Column("due_date")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private LocalDateTime dueDate;

    @Column("priority")
    @CassandraType(type = CassandraType.Name.INT)
    private int priority = 0;

    @Column("completed")
    @CassandraType(type = CassandraType.Name.BOOLEAN)
    private boolean completed = false;

    public TodoById() {
    }

    public TodoById(UUID todoId, String title, String description, LocalDateTime dueDate, int priority, boolean completed) {
        this.todoId = todoId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }

    public UUID getTodoId() {
        return todoId;
    }

    public void setTodoId(UUID todoId) {
        this.todoId = todoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

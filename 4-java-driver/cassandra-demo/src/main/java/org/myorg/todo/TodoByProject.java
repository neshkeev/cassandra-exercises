package org.myorg.todo;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(value = "todos_by_project")
public class TodoByProject {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 0, name = "project")
    @CassandraType(type = Name.TEXT)
    private String project;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordinal = 1, name = "due_date", ordering = Ordering.DESCENDING)
    @CassandraType(type = Name.TIMESTAMP)
    private LocalDateTime dueDate;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordinal = 2, name = "priority", ordering = Ordering.ASCENDING)
    @CassandraType(type = Name.INT)
    private int priority = 0;

    @Column("todo_id")
    @CassandraType(type = Name.UUID)
    private UUID todoId;

    @Column("title")
    @CassandraType(type = Name.TEXT)
    private String title;

    @Column("completed")
    @CassandraType(type = Name.BOOLEAN)
    private boolean completed = false;

    public TodoByProject() {
    }

    public TodoByProject(String project, LocalDateTime dueDate, int priority, UUID todoId, String title, boolean completed) {
        this.project = project;
        this.dueDate = dueDate;
        this.priority = priority;
        this.todoId = todoId;
        this.title = title;
        this.completed = completed;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

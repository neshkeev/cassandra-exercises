package org.myorg.todo.factory.statemachine;

import org.myorg.todo.TodoById;
import org.myorg.todo.TodoByProject;
import org.myorg.todo.factory.statemachine.TodoFactory.Exists.False;
import org.myorg.todo.factory.statemachine.TodoFactory.Exists.True;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class TodoFactory {
    private final UUID todoId;
    private final String title;
    private final String description;
    private final LocalDateTime dueDate;
    private final int priority;
    private final boolean completed;
    private final String project;

    private TodoFactory(final UUID todoId,
                        final String title,
                        final String description,
                        final LocalDateTime dueDate,
                        final int priority,
                        final boolean completed,
                        final String project) {
        this.todoId = todoId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        this.project = project;
    }

    public TodoById getTodoById() {
        return new TodoById(todoId, title, description, dueDate, priority, completed);
    }

    public TodoByProject getTodoByProject() {
        return new TodoByProject(project, dueDate, priority, todoId, title, completed);
    }

    public static Builder<False, False, False, False> builder() {
        return new Builder<>();
    }

    public static TodoFactory of(Builder<True, True, True, True> builder) {
        return new TodoFactory(builder.todoId, builder.title, builder.description, builder.dueDate, builder.priority, builder.completed, builder.project);
    }

    public static abstract class Exists {
        private Exists() {
        }

        public static final class True extends Exists {
        }

        public static final class False extends Exists {
        }
    }

    public static class Builder<TODO_ID extends Exists, PROJECT extends Exists, DUE_DATE extends Exists, PRIORITY extends Exists> {
        private UUID todoId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private int priority;
        private boolean completed;
        private String project;

        private Builder() {}

        public Builder(UUID todoId, String title, String description, LocalDateTime dueDate, int priority, boolean completed, String project) {
            this.todoId = todoId;
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.priority = priority;
            this.completed = completed;
            this.project = project;
        }

        public Builder<True, PROJECT, DUE_DATE, PRIORITY> setTodoId(UUID todoId) {
            return new Builder<>(todoId, title, description, dueDate, priority, completed, project);
        }

        public Builder<TODO_ID, True, DUE_DATE, PRIORITY> setProject(String project) {
            return new Builder<>(todoId, title, description, dueDate, priority, completed, project);
        }

        public Builder<TODO_ID, PROJECT, True, PRIORITY> setDueDate(LocalDateTime dueDate) {
            return new Builder<>(todoId, title, description, dueDate, priority, completed, project);
        }

        public Builder<TODO_ID, PROJECT, DUE_DATE, True> setPriority(int priority) {
            return new Builder<>(todoId, title, description, dueDate, priority, completed, project);
        }

        public Builder<TODO_ID, PROJECT, DUE_DATE, PRIORITY> setTitle(String title) {
            return new Builder<>(todoId, title, description, dueDate, priority, completed, project);
        }

        public Builder<TODO_ID, PROJECT, DUE_DATE, PRIORITY> setDescription(String description) {
            return new Builder<>(todoId, title, description, dueDate, priority, completed, project);
        }

        public Builder<TODO_ID, PROJECT, DUE_DATE, PRIORITY> setCompleted(boolean completed) {
            return new Builder<>(todoId, title, description, dueDate, priority, completed, project);
        }
    }
}
package org.myorg.todo.factory;

import org.myorg.todo.TodoById;
import org.myorg.todo.TodoByProject;

import java.time.LocalDateTime;
import java.util.UUID;

public final class TodoFactory {
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

    public static TodoFactoryBuilder builder() {
        return new TodoFactoryBuilder();
    }

    public static final class TodoFactoryBuilder {
        private UUID todoId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private int priority;
        private boolean completed;
        private String project;

        private TodoFactoryBuilder() {
        }

        public TodoFactoryBuilder setTodoId(UUID todoId) {
            this.todoId = todoId;
            return this;
        }

        public TodoFactoryBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public TodoFactoryBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public TodoFactoryBuilder setDueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public TodoFactoryBuilder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public TodoFactoryBuilder setCompleted(boolean completed) {
            this.completed = completed;
            return this;
        }

        public TodoFactoryBuilder setProject(String project) {
            this.project = project;
            return this;
        }

        public TodoFactory createTodoFactory() {
            return new TodoFactory(todoId, title, description, dueDate, priority, completed, project);
        }
    }
}

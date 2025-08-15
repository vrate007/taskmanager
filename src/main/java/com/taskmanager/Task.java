package com.taskmanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private static long nextId = 1;
    private final long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(String title, TaskPriority priority) {
        this.id = nextId++;
        this.title = title;
        this.status = TaskStatus.NEW;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now(); // Оновлюємо час при зміні статусу
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now(); // Оновлюємо час при зміні пріоритету
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String toString() {
        // Створюємо форматування
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        // Форматуємо час
        String formattedCreatedAt = createdAt.format(formatter);
        String formattedUpdatedAt = updatedAt.format(formatter);

        return String.format(
                "ID: %d\n" +
                        "Назва: %s\n" +
                        "Статус: %s\n" +
                        "Пріоритет: %s\n" +
                        "Створено: %s\n" +
                        "Оновлено: %s",
                id, title, status, priority, formattedCreatedAt, formattedUpdatedAt
        );
    }
}
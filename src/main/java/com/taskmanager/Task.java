package com.taskmanager;

public class Task {
    private static long nextId = 1;
    private final long id;
    private String title;
    private TaskStatus status; // Додаємо поле статусу
    private TaskPriority priority; // Додаємо поле пріорітету

    public Task(String title) {
        this.id = nextId++;
        this.title = title;
        this.status = TaskStatus.NEW; // Нова задача починається зі статусу NEW
        this.priority = TaskPriority.MEDIUM; // Нова задача починається із пріорітету MEDIUM

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
    }

    public TaskPriority getProirity() {
        return priority;
    }
    public void setProirity(TaskPriority proirity) {
        this.priority = proirity;
    }

    public String toString() {
        return "Task [id=" + id + ", title=" + title + ", status=" + status + ", priority=" + priority + "]";
    }

    public TaskPriority getPriority() {
        return priority;
    }
}
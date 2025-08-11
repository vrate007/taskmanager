package com.taskmanager;

public class Task {
    private static long nextId = 1;
    private String title;

    public Task(String title) {
        this.id = nextId++;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return "Task [id=" + id + ", title=" + title + "]";
    }
}
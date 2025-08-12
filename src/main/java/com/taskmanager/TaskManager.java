package com.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(String title) {
        if (title != null && !title.trim().isEmpty()) {
            Task newTask = new Task(title);
            tasks.add(newTask);
            System.out.println("Задача додана: " + newTask);
        } else {
            System.out.println("Помилка: Назва задачі не може бути порожньою.");
        }
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    // видалення
    public boolean removeTask(long id) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getId() == id) {
                iterator.remove();
                return true; // Задача знайдена і видалена
            }
        }
        return false; // Задача не знайдена
    }
}
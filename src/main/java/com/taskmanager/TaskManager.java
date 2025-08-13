package com.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(String title, TaskPriority priority) {
        if (title != null && !title.trim().isEmpty()) {
            Task newTask = new Task(title, priority);
            tasks.add(newTask);
            System.out.println("Задача додана: " + newTask);
        } else {
            System.out.println("Помилка: Назва задачі не може бути порожньою.");
        }
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public boolean removeTask(long id) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean updateTaskStatus(long id, TaskStatus newStatus) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setStatus(newStatus);
                System.out.println("Статус задачі з ID " + id + " змінено на " + newStatus);
                return true;
            }
        }
        return false;
    }

    public boolean updateTaskPriority(long id, TaskPriority newPriority) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setPriority(newPriority);
                System.out.println("Пріорітут задачі з ID " + id + " змінено на " + newPriority);
                return true;
            }
        }
        return false;
    }

    // Новий метод для пошуку за назвою
    public List<Task> findTasksByTitle(String title) {
        List<Task> foundTasks = new ArrayList<>();
        if (title == null || title.trim().isEmpty()) {
            return foundTasks;
        }
        String searchTitle = title.toLowerCase();
        for (Task task : tasks) {
            if (task.getTitle().toLowerCase().contains(searchTitle)) {
                foundTasks.add(task);
            }
        }
        return foundTasks;
    }
}
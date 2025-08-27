package com.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileNotFoundException;

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

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

    //пошуку за назвою
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


    public void saveTasksToFile(String filsaveTM) {
        try (FileWriter fileWriter = new FileWriter(filsaveTM, false);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
             printWriter.println(Task.getHeader());
            for (Task task : tasks) { // Перебираємо кожен об'єкт Task
                printWriter.println(task.toString());
            }
            System.out.println("Задачі успішно збережено у файл: " + filsaveTM);
        } catch (IOException e) {
            System.err.println("Помилка при збереженні файлу: " + e.getMessage());
        }
    }

    public void loadTasksFromFile(String filePath) {
        this.tasks.clear();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Пропускаємо рядок заголовків
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");

                if (values.length == 6) {
                    long id = Long.parseLong(values[0].trim());
                    String title = values[1].trim();
                    TaskStatus status = TaskStatus.valueOf(values[2].trim());
                    TaskPriority priority = TaskPriority.valueOf(values[3].trim());

                    // Парсимо дати з рядків у LocalDateTime
                    LocalDateTime createdAt = LocalDateTime.parse(values[4].trim(), formatter);
                    LocalDateTime updatedAt = LocalDateTime.parse(values[5].trim(), formatter);

                    // Тепер передаємо всі необхідні параметри
                    Task task = new Task(id, title, status, priority, createdAt, updatedAt);
                    this.tasks.add(task);
                }
            }
            System.out.println("Задачі успішно завантажено з файлу: " + filePath);
        } catch (FileNotFoundException e) {
            System.err.println("Файл не знайдено: " + e.getMessage());
        } catch (Exception e) { // Обробка інших винятків, наприклад, некоректного формату даних
            System.err.println("Помилка при зчитуванні даних з файлу: " + e.getMessage());
            e.printStackTrace(); // Виводимо повний стек викликів для діагностики
        }
    }
}


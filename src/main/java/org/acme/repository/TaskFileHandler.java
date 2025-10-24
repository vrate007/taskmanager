package org.acme.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.Task;
import org.acme.domain.TaskPriority;
import org.acme.domain.TaskStatus;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Клас є біном CDI для автоматичної інжекції
@ApplicationScoped
public class TaskFileHandler implements DataStoreHandler<Task> {

    // Шлях до файлу визначений як константа, оскільки немає конструктора з параметрами
    private static final String FILE_PATH = "tasks.csv";

    // Форматувальник для читання/запису дат
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public TaskFileHandler() {
        // Конструктор за замовчуванням для CDI
    }


    // --- C / U (Save All) ---
    @Override
    public void saveAll(List<Task> tasks) {
        try (FileWriter fileWriter = new FileWriter(FILE_PATH, false);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            printWriter.println(Task.getHeader());

            for (Task task : tasks) {
                printWriter.println(task.toString());
            }
            System.out.println("Задачі успішно збережено у файл: " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Помилка при збереженні файлу: " + e.getMessage());
        }
    }

    // --- R (Load All) ---
    @Override
    public List<Task> loadAll() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("Файл " + FILE_PATH + " не знайдено. Буде створено новий.");
            return tasks;
        }

        long maxId = 0;

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Пропускаємо заголовок
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split("\\s*,\\s*");

                if (values.length == 6) {
                    try {
                        long id = Long.parseLong(values[0]);
                        String title = values[1];
                        TaskStatus status = TaskStatus.valueOf(values[2]);
                        TaskPriority priority = TaskPriority.valueOf(values[3]);
                        LocalDateTime createdAt = LocalDateTime.parse(values[4], formatter);
                        LocalDateTime updatedAt = LocalDateTime.parse(values[5], formatter);

                        tasks.add(new Task(id, title, status, priority, createdAt, updatedAt));
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (Exception e) {
                        System.err.println("Помилка при парсингу рядка (буде проігнорована): " + line);
                        // Продовжуємо, щоб не зупиняти завантаження через один некоректний рядок
                    }
                }
            }
            Task.setNextId(maxId);
            System.out.println("Задачі успішно завантажено з файлу: " + FILE_PATH);
        } catch (FileNotFoundException e) {
            System.err.println("Файл не знайдено: " + e.getMessage());
        }
        return tasks;
    }
}

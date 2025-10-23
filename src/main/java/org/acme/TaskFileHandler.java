package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@ApplicationScoped // Ця анотація тепер працюватиме коректно
public class TaskFileHandler implements FileHandler<Task> {

    // 🌟 ЗМІНА 1: Робимо шлях до файлу статичною константою,
    // оскільки ми не можемо передати його через конструктор CDI.
    private static final String FILE_PATH = "tasks.csv";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    // ❌ ЗМІНА 2: ВИДАЛЯЄМО конструктор з аргументом (це порушує CDI)
    /*
    private final String filePath;
    public TaskFileHandler(String filePath) {
        this.filePath = filePath;
    }
    */

    // 🌟 ТА ЗАЛИШАЄМО КОНСТРУКТОР ЗА ЗАМОВЧУВАННЯМ (або не оголошуємо жодного)
    public TaskFileHandler() {
        // Конструктор за замовчуванням
    }


    // --- C (Create / Save All) ---

    @Override
    public void save(List<Task> tasks) {
        try (FileWriter fileWriter = new FileWriter(FILE_PATH, false); // 🌟 Використовуємо константу
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

    // --- R (Read / Load All) ---

    @Override
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH); // 🌟 Використовуємо константу
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
                        System.err.println("Помилка при парсингу рядка: " + line);
                        System.err.println("Причина: " + e.getMessage());
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

    // --- R (Read / Find By ID) ---
    // (Не потребує змін)
    @Override
    public Optional<Task> findById(long id) {
        return load().stream()
                .filter(task -> task.getId() == id)
                .findFirst();
    }

    // --- U (Update) ---
    // (Не потребує змін)
    @Override
    public void update(Task updatedTask) {
        List<Task> tasks = load();
        boolean found = false;

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updatedTask.getId()) {
                updatedTask.setUpdatedAt(LocalDateTime.now());
                tasks.set(i, updatedTask);
                found = true;
                break;
            }
        }

        if (found) {
            save(tasks);
            System.out.println("Задача з ID " + updatedTask.getId() + " успішно оновлена.");
        } else {
            System.err.println("Помилка оновлення: Задача з ID " + updatedTask.getId() + " не знайдена.");
        }
    }

    // --- D (Delete) ---
    // (Не потребує змін)
    @Override
    public void delete(long taskId) {
        List<Task> tasks = load();
        int initialSize = tasks.size();

        List<Task> updatedTasks = tasks.stream()
                .filter(task -> task.getId() != taskId)
                .collect(Collectors.toList());

        if (updatedTasks.size() < initialSize) {
            save(updatedTasks);
            System.out.println("Задача з ID " + taskId + " успішно видалена.");
        } else {
            System.err.println("Помилка видалення: Задача з ID " + taskId + " не знайдена.");
        }
    }
}
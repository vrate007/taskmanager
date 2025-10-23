package org.acme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private static long nextId = 1;
    private final long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Спільний форматувальник, щоб уникнути дублювання
    private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    // --- 1. Конструктор за замовчуванням (ДЛЯ ФРЕЙМВОРКІВ) ---
    public Task() {
        this.id = -1; // Тимчасовий ID. Нові об'єкти створюйте через основний конструктор!
    }

    // Конструктор для нових задач
    public Task(String title, TaskPriority priority) {
        this.id = nextId++;
        this.title = title;
        this.status = TaskStatus.NEW;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Конструктор для завантаження існуючих задач з файлу
    public Task(long id, String title, TaskStatus status, TaskPriority priority, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- Управління ID ---

    public static void setNextId(long maxId) {
        if (maxId >= nextId) {
            nextId = maxId + 1;
        }
    }

    // --- Геттери та Сеттери ---

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // 🌟 Додано сеттер для title
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 🌟 Додано сеттер для createdAt (для гнучкості при завантаженні/оновленні)
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // 🌟 Додано сеттер для updatedAt (корисно для оновлення в TaskFileHandler)
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // --- Персистентність (FileHandler) ---

    public static String getHeader() {
        return "id,title,status,priority,createdAt,updatedAt";
    }

    @Override
    public String toString() {
        String formattedCreatedAt = createdAt.format(FILE_FORMATTER);
        String formattedUpdatedAt = updatedAt.format(FILE_FORMATTER);

        return String.format(
                "%d," +
                        "%s," +
                        "%s," +
                        "%s," +
                        "%s," +
                        "%s",
                id, title, status, priority, formattedCreatedAt, formattedUpdatedAt
        );
    }

    // --- 🌟 Порівняння об'єктів (КРИТИЧНО ВАЖЛИВО) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // Ми вважаємо дві задачі однаковими, якщо їхні ID співпадають
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        // Хеш-код базується лише на ID
        return Objects.hash(id);
    }
}
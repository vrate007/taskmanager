package org.acme;

import java.util.List;
import java.util.Optional; // 🌟 Імпорт Optional

public interface TaskManagerInterface {

    // --- C (Create) ---
    // 🌟 Повертаємо створений об'єкт Task
    Task addTask(String title, TaskPriority priority);

    // --- R (Read) ---
    List<Task> getAllTasks();
    // 🌟 Додаємо метод для читання за ID з Optional
    Optional<Task> getTaskById(long id);

    // --- U (Update) ---

    // 🌟 Узагальнений метод для оновлення. Повертаємо Optional<Task>
    Optional<Task> updateTask(long id, String newTitle, TaskStatus newStatus, TaskPriority newPriority);

    // Існуючі методи оновлення можна залишити для спрощення бізнес-логіки
    boolean updateTaskStatus(long id, TaskStatus newStatus);
    boolean updateTaskPriority(long id, TaskPriority newPriority);

    // --- D (Delete) ---
    boolean removeTask(long id);

    // --- Пошук та Фільтрація ---
    List<Task> findTasksByTitle(String title);
    List<Task> filterTasksByStatus(TaskStatus status);
    List<Task> filterTasksByPriority(TaskPriority priority);

    // --- Сортування ---
    List<Task> sortTasksByCreatedAt();
    List<Task> sortTasksByPriority();
    List<Task> sortTasksByStatus();
}
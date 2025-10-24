package org.acme.domain;

import java.util.List;
import java.util.Optional;

/**
 * Інтерфейс, що визначає контракт Репозиторію для сутностей Task.
 * Містить всі CRUD-операції та методи доступу до даних.
 */
public interface TaskRepository {

    // --- C (Create) ---
    Task addTask(String title, TaskPriority priority);

    // --- R (Read) ---
    List<Task> getAllTasks();
    Optional<Task> getTaskById(long id);

    // --- U (Update) ---
    // Узагальнений метод для оновлення
    Optional<Task> updateTask(long id, String newTitle, TaskStatus newStatus, TaskPriority newPriority);

    // Часткові оновлення
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

    // Методи для керування внутрішнім станом (ініціалізація кешу)
    void setInitialTasks(List<Task> initialTasks);
}

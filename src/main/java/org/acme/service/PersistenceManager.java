package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.acme.domain.Task;
import org.acme.domain.TaskPriority;
import org.acme.domain.TaskRepository;
import org.acme.domain.TaskStatus;
import org.acme.repository.DataStoreHandler;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
public class PersistenceManager {

    // Інжектуємо вузький інтерфейс DataStoreHandler (I/O)
    @Inject
    DataStoreHandler<Task> dataStoreHandler;

    // Інжектуємо TaskRepository (який реалізований TaskManager)
    @Inject
    TaskRepository taskRepository;

    /**
     * Порожній метод, оскільки ініціалізація перенесена в TaskInitializer.
     * Залишаємо його для коректної роботи CDI.
     */
    @PostConstruct
    void init() {
        System.out.println("PersistenceManager: CDI-ініціалізація успішна. Персистентність готова.");
    }

    //setInitialTasks тепер є звичайним методом, який викликається TaskInitializer.
    public void setInitialTasks(List<Task> initialTasks) {
        // Делегуємо фактичну ініціалізацію кешу TaskManager
        taskRepository.setInitialTasks(initialTasks);

        // Після ініціалізації (якщо це перший запуск), зберігаємо тестові дані у файл
        dataStoreHandler.saveAll(taskRepository.getAllTasks());
    }

    /**
     * Обгортка для виконання дії в TaskManager та збереження кешу у файл,
     * якщо модифікація була успішною.
     * @param action Дія, що повертає об'єкт або Optional/boolean.
     * @return Результат дії.
     */
    private <T> T executeAndSave(Supplier<T> action) {
        T result = action.get();
        // Після будь-якої модифікації зберігаємо весь кеш у файл
        dataStoreHandler.saveAll(taskRepository.getAllTasks());
        return result;
    }

    // --- C (Create) ---
    public Task addTask(String title, TaskPriority priority) {
        return executeAndSave(() -> taskRepository.addTask(title, priority));
    }

    // --- U (Update) ---
    public Optional<Task> updateTask(long id, String newTitle, TaskStatus newStatus, TaskPriority newPriority) {
        Optional<Task> result = taskRepository.updateTask(id, newTitle, newStatus, newPriority);

        if (result.isPresent()) {
            // Зберігаємо, лише якщо оновлення було успішним
            dataStoreHandler.saveAll(taskRepository.getAllTasks());
        }
        return result;
    }

    public boolean updateTaskStatus(long id, TaskStatus newStatus) {
        return executeAndSave(() -> taskRepository.updateTaskStatus(id, newStatus));
    }

    public boolean updateTaskPriority(long id, TaskPriority newPriority) {
        return executeAndSave(() -> taskRepository.updateTaskPriority(id, newPriority));
    }

    // --- D (Delete) ---
    public boolean removeTask(long id) {
        return executeAndSave(() -> taskRepository.removeTask(id));
    }

    // --- R (Read - Делегування) ---

    // ПРИМІТКА: Методи читання не змінюють стан, тому не використовують executeAndSave.

    public List<Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    public Optional<Task> getTaskById(long id) {
        return taskRepository.getTaskById(id);
    }

    public List<Task> findTasksByTitle(String title) {
        return taskRepository.findTasksByTitle(title);
    }

    public List<Task> filterTasksByStatus(TaskStatus status) {
        return taskRepository.filterTasksByStatus(status);
    }

    public List<Task> filterTasksByPriority(TaskPriority priority) {
        return taskRepository.filterTasksByPriority(priority);
    }

    public List<Task> sortTasksByCreatedAt() {
        return taskRepository.sortTasksByCreatedAt();
    }

    public List<Task> sortTasksByPriority() {
        return taskRepository.sortTasksByPriority();
    }

    public List<Task> sortTasksByStatus() {
        return taskRepository.sortTasksByStatus();
    }
}

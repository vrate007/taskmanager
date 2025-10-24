package org.acme.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.Task;
import org.acme.domain.TaskPriority;
import org.acme.domain.TaskRepository;
import org.acme.domain.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 🌟 ЗМІНА 1: Використовуємо ApplicationScoped для CDI
// 🌟 ЗМІНА 2: Реалізуємо TaskRepository
@ApplicationScoped
public class TaskManager implements TaskRepository {

    private final List<Task> tasks = Collections.synchronizedList(new ArrayList<>());

    // Метод для ініціалізації кешу ззовні (викликається PersistenceManager)
    // Це єдиний спосіб, яким дані потрапляють у Tasks
    public void setInitialTasks(List<Task> initialTasks) {
        this.tasks.clear();
        this.tasks.addAll(initialTasks);
    }

    // --- C (Create) ---
    @Override
    public Task addTask(String title, TaskPriority priority) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва задачі не може бути порожньою.");
        }
        Task newTask = new Task(title, priority);
        tasks.add(newTask);
        return newTask;
    }

    // --- R (Read) ---
    @Override
    public List<Task> getAllTasks() {
        // Повертаємо копію для безпеки потоків та уникнення зовнішньої модифікації
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    @Override
    public Optional<Task> getTaskById(long id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    @Override
    public List<Task> findTasksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        final String searchTitle = title.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(searchTitle))
                .collect(Collectors.toList());
    }

    // --- U (Update) ---
    @Override
    public Optional<Task> updateTask(long id, String newTitle, TaskStatus newStatus, TaskPriority newPriority) {
        Optional<Task> taskOpt = getTaskById(id);

        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            boolean changed = false;

            if (newTitle != null && !newTitle.isEmpty() && !newTitle.equals(task.getTitle())) {
                task.setTitle(newTitle);
                changed = true;
            }
            if (newStatus != null && newStatus != task.getStatus()) {
                task.setStatus(newStatus);
                changed = true;
            }
            if (newPriority != null && newPriority != task.getPriority()) {
                task.setPriority(newPriority);
                changed = true;
            }

            if (changed) {
                // setUpdatedAt вже викликається в сеттерах, але гарантуємо тут
                task.setUpdatedAt(LocalDateTime.now());
            }
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateTaskStatus(long id, TaskStatus newStatus) {
        Optional<Task> taskOpt = getTaskById(id);
        if (taskOpt.isPresent()) {
            taskOpt.get().setStatus(newStatus);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTaskPriority(long id, TaskPriority newPriority) {
        Optional<Task> taskOpt = getTaskById(id);
        if (taskOpt.isPresent()) {
            taskOpt.get().setPriority(newPriority);
            return true;
        }
        return false;
    }

    // --- D (Delete) ---
    @Override
    public boolean removeTask(long id) {
        // ВИПРАВЛЕНО: Використовуємо removeIf для видалення
        return tasks.removeIf(t -> t.getId() == id);
    }

    // --- Фільтрація ---
    @Override
    public List<Task> filterTasksByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> filterTasksByPriority(TaskPriority priority) {
        return tasks.stream()
                .filter(t -> t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    // --- Сортування ---
    @Override
    public List<Task> sortTasksByCreatedAt() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> sortTasksByPriority() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> sortTasksByStatus() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getStatus))
                .collect(Collectors.toList());
    }
}

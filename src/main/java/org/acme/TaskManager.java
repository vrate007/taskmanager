package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 1. 🌟 Додаємо анотації Quarkus: ApplicationScoped та Inject
@ApplicationScoped
public class TaskManager implements TaskManagerInterface {

    // 2. 🌟 Змінюємо поле: це буде кеш, і ми повинні мати доступ до нього
    private List<Task> tasks;

    // 3. 🌟 Вводимо залежність: TaskFileHandler для персистентності
    @Inject
    TaskFileHandler fileHandler;

    // 4. 🌟 Метод, що виконується після створення об'єкта: Завантаження даних
    @PostConstruct
    void init() {
        // При старті завантажуємо всі задачі з файлу
        this.tasks = fileHandler.load();
        if (this.tasks == null) {
            // Використовуємо синхронізований список для безпеки
            this.tasks = Collections.synchronizedList(new ArrayList<>());
        } else {
            // Перетворюємо на синхронізований список, якщо успішно завантажено
            this.tasks = Collections.synchronizedList(new ArrayList<>(this.tasks));
        }
    }

    // 5. ❌ Видаляємо допоміжний метод setAllTasks та видаляємо поле tasks = new ArrayList<>()

    // --- C (Create) ---
    @Override
    public Task addTask(String title, TaskPriority priority) {
        if (title == null || title.trim().isEmpty()) {
            // У бізнес-шарі краще кидати виняток, а не друкувати в System.out
            throw new IllegalArgumentException("Назва задачі не може бути порожньою.");
        }
        Task newTask = new Task(title, priority);
        tasks.add(newTask);
        fileHandler.save(tasks); // 🌟 Зберігаємо оновлений список у файл
        return newTask;
    }

    // --- R (Read) ---
    @Override
    public List<Task> getAllTasks() {
        // Повертаємо незмінну копію списку, щоб уникнути зовнішньої модифікації кешу
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    // 🌟 Додаємо реалізацію getTaskById
    @Override
    public Optional<Task> getTaskById(long id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    // --- D (Delete) ---
    // Перейменовуємо, як в інтерфейсі, і додаємо збереження
    @Override
    public boolean removeTask(long id) {
        // Використовуємо removeIf для більш чистого коду
        boolean removed = tasks.removeIf(t -> t.getId() == id);
        if (removed) {
            fileHandler.save(tasks); // 🌟 Зберігаємо після видалення
        }
        return removed;
    }

    // --- U (Update) ---

    // 🌟 Реалізація загального методу оновлення з інтерфейсу
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
                // Встановлення updatedAt відбувається у сеттерах Task, але тут можна гарантувати
                task.setUpdatedAt(LocalDateTime.now());
                fileHandler.save(tasks); // 🌟 Зберігаємо оновлений список
            }
            return Optional.of(task);
        }
        return Optional.empty();
    }

    // Часткові оновлення (залишаємо для сумісності з інтерфейсом)
    @Override
    public boolean updateTaskStatus(long id, TaskStatus newStatus) {
        Optional<Task> taskOpt = getTaskById(id);
        if (taskOpt.isPresent()) {
            taskOpt.get().setStatus(newStatus);
            fileHandler.save(tasks); // 🌟 Зберігаємо
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTaskPriority(long id, TaskPriority newPriority) {
        Optional<Task> taskOpt = getTaskById(id);
        if (taskOpt.isPresent()) {
            taskOpt.get().setPriority(newPriority);
            fileHandler.save(tasks); // 🌟 Зберігаємо
            return true;
        }
        return false;
    }

    // --- Пошук та Фільтрація ---
    @Override
    public List<Task> findTasksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        final String searchTitle = title.toLowerCase();
        // 🌟 Оптимізована логіка через Stream API
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(searchTitle))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> filterTasksByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> filterTasksByPriority(TaskPriority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    // --- Сортування ---

    // Сортуємо за датою створення (від найстарішої до найновішої)
    @Override
    public List<Task> sortTasksByCreatedAt() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .collect(Collectors.toList());
    }

    // Сортуємо за пріоритетом (за спаданням: HIGH, MEDIUM, LOW, якщо enum визначено так)
    @Override
    public List<Task> sortTasksByPriority() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .collect(Collectors.toList());
    }

    // Сортуємо за статусом
    @Override
    public List<Task> sortTasksByStatus() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getStatus))
                .collect(Collectors.toList());
    }
}
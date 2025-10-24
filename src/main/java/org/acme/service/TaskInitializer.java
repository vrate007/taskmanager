package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import io.quarkus.runtime.StartupEvent;
import org.acme.domain.Task;
import org.acme.domain.TaskPriority;
import org.acme.domain.TaskRepository;
import org.acme.domain.TaskStatus;
import org.acme.repository.DataStoreHandler;
import org.acme.repository.TaskManager;

import java.util.List;

/**
 * Клас для ініціалізації даних при першому запуску програми.
 * Перевіряє наявність даних у файлі і, якщо їх немає, створює тестовий набір.
 */
@ApplicationScoped
public class TaskInitializer {

    // Інжектуємо DataStoreHandler для завантаження початкових даних
    @Inject
    DataStoreHandler<Task> dataStoreHandler;

    // Інжектуємо TaskRepository для доступу до методів TaskManager
    @Inject
    TaskRepository taskRepository;

    // Інжектуємо PersistenceManager для збереження тестових даних у файл
    @Inject
    PersistenceManager persistenceManager;

    /**
     * Метод, який викликається автоматично після завершення запуску Quarkus.
     */
    void onStart(@Observes StartupEvent ev) {

        System.out.println(">>> TaskInitializer: Запуск ініціалізації даних...");

        // 1. Завантажуємо дані з файлу
        List<Task> initialTasks = dataStoreHandler.loadAll();

        if (initialTasks.isEmpty()) {

            System.out.println(">>> TaskInitializer: Сховище порожнє. Генеруємо тестові дані.");

            // 2. Створюємо тестові дані (використовуючи TaskRepository/PersistenceManager)
            // Викликаємо addTask, який через PersistenceManager збереже дані у файл.
            persistenceManager.addTask("Купити хліб та молоко", TaskPriority.HIGH);

            // Оскільки setStatus викликає save через PersistenceManager,
            // ми повинні обробляти це обережно або просто створити тестові дані.

            // Створюємо та отримуємо задачу для подальшого редагування статусу
            Task task2 = persistenceManager.addTask("Доробити звіт по проєкту", TaskPriority.MEDIUM);
            persistenceManager.updateTaskStatus(task2.getId(), TaskStatus.IN_PROGRESS);

            persistenceManager.addTask("Перевірити архітектуру CDI", TaskPriority.LOW);

            // 3. Зберігаємо всі тестові дані у файл (PersistenceManager.addTask вже викликає saveAll)
            // Не потрібно викликати saveAllTasks() тут, оскільки addTask це вже зробив.

            System.out.println(">>> TaskInitializer: Тестові дані успішно створено та збережено.");
        } else {
            // 4. Якщо дані є, передаємо їх у TaskManager для ініціалізації кешу.
            ((TaskManager)taskRepository).setInitialTasks(initialTasks);
            System.out.println(">>> TaskInitializer: Завантажено " + initialTasks.size() + " задач із файлу.");
        }
    }
}

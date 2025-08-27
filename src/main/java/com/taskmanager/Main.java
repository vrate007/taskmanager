package com.taskmanager;

import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск програми 'Менеджер задач'...");
        System.out.println("--------------------");
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        String userInput;

        // Додамо декілька задач для тестування
        manager.addTask("Вивчити Java", TaskPriority.HIGH);
        System.out.println("--------------------");
        manager.addTask("Написати звіт", TaskPriority.MEDIUM);
        System.out.println("--------------------");
        manager.addTask("Написати код", TaskPriority.LOW);
        System.out.println("--------------------");


        // Викликаємо метод збереження у класі TaskManager
        manager.saveTasksToFile("tasks.csv");


        do {
            System.out.println("\nОпції:");
            System.out.println("1. Додати нову задачу");
            System.out.println("2. Показати всі задачі");
            System.out.println("3. Видалити задачу");
            System.out.println("4. Змінити статус задачі");
            System.out.println("5. Змінити пріорітет задачі");
            System.out.println("6. Знайти задачу за назвою");
            System.out.println("7. Вийти");
            System.out.print("Ваш вибір: ");

            userInput = scanner.nextLine();

            switch (userInput) {
                case "1":
                    System.out.print("Введіть назву задачі: ");
                    String taskTitle = scanner.nextLine();
                    System.out.print("Введіть пріоритет (LOW, MEDIUM, HIGH): ");
                    String priorityInput = scanner.nextLine().toUpperCase();
                    try {
                        TaskPriority priority = TaskPriority.valueOf(priorityInput);
                        manager.addTask(taskTitle, priority);
                        manager.saveTasksToFile("tasks.csv"); // Автоматичне збереження
                    } catch (IllegalArgumentException e) {
                        System.out.println("Помилка: Некоректний пріоритет. Використовуйте LOW, MEDIUM або HIGH.");
                    }
                    break;
                case "2":
                    System.out.println("\n--- Список задач ---");
                    List<Task> allTasks = manager.getAllTasks();
                    for (int i = 0; i < allTasks.size(); i++) {
                        System.out.println(allTasks.get(i));
                        if (i < allTasks.size() - 1) {
                            System.out.println("--------------------"); // Розділювач
                        }
                    }
                    break;
                case "3":
                    try {
                        System.out.print("Введіть ID задачі для видалення: ");
                        long taskId = Long.parseLong(scanner.nextLine());
                        if (manager.removeTask(taskId)) {
                            System.out.println("Задача з ID " + taskId + " успішно видалена.");
                            manager.saveTasksToFile("tasks.csv"); // Автоматичне збереження
                        } else {
                            System.out.println("Помилка: Задача з ID " + taskId + " не знайдена.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Помилка: ID повинен бути числом.");
                    }
                    break;
                case "4":
                    try {
                        System.out.print("Введіть ID задачі для зміни статусу: ");
                        long taskId = Long.parseLong(scanner.nextLine());
                        System.out.print("Введіть новий статус (NEW, IN_PROGRESS, COMPLETED): ");
                        String statusInput = scanner.nextLine().toUpperCase();
                        TaskStatus newStatus = TaskStatus.valueOf(statusInput);
                        if (!manager.updateTaskStatus(taskId, newStatus)) {
                            System.out.println("Помилка: Задача з ID " + taskId + " не знайдена.");
                        } else {
                            manager.saveTasksToFile("tasks.csv"); // Автоматичне збереження
                        }
                        if (!manager.updateTaskStatus(taskId, newStatus)) {
                            System.out.println("Помилка: Задача з ID " + taskId + " не знайдена.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Помилка: ID повинен бути числом.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Помилка: Некоректний статус. Використовуйте NEW, IN_PROGRESS або COMPLETED.");
                    }

                    break;
                case "5":
                    try {
                        System.out.print("Введіть ID задачі для зміни пріорітету: ");
                        long taskId = Long.parseLong(scanner.nextLine());
                        System.out.print("Введіть новий пріорітет задачі (LOW, MEDIUM, HIGH): ");
                        String updatedPriorityInput = scanner.nextLine().toUpperCase();
                        TaskPriority newPriority = TaskPriority.valueOf(updatedPriorityInput);
                        if (!manager.updateTaskPriority(taskId, newPriority)) {
                            System.out.println("Помилка: Задача з ID " + taskId + " не знайдена.");
                        } else {
                            manager.saveTasksToFile("tasks.csv"); // Автоматичне збереження
                        }
                        if (!manager.updateTaskPriority(taskId, newPriority)) {
                            System.out.println("Помилка: Задача з ID " + taskId + " не знайдена.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Помилка: ID повинен бути числом.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Помилка: Некоректний статус. Використовуйте NEW, IN_PROGRESS або COMPLETED.");
                    }
                    break;
                case "6":
                    System.out.print("Введіть назву або частину назви для пошуку: ");
                    String searchTitle = scanner.nextLine();
                    List<Task> foundTasks = manager.findTasksByTitle(searchTitle);
                    if (foundTasks.isEmpty()) {
                        System.out.println("Задачі з такою назвою не знайдено.");
                    } else {
                        System.out.println("\n--- Знайдені задачі ---");
                        foundTasks.forEach(System.out::println);
                    }
                    break;
                case "7":
                    System.out.print("Зберегти зміни у файл? (Y/N): ");
                    String saveInput = scanner.nextLine().toUpperCase();
                    if (saveInput.equals("Y")) {
                        manager.saveTasksToFile("tasks.csv"); // Зберігаємо у файл
                    }
                    System.out.println("Завершення програми.");
                    break;
                default:
                    System.out.println("Некоректний вибір. Спробуйте ще раз.");
                    break;
            }
        } while (!userInput.equals("7")); // Умова циклу перевіряє вихід за номером 7
        scanner.close();
    }
}
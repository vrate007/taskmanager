package com.taskmanager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск програми 'Менеджер задач'...");

        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        String userInput;

        // Додамо декілька задач для тестування
        manager.addTask("Вивчити Java");
        manager.addTask("Написати код");

        // Основний цикл програми
        do {
            System.out.println("\nОпції:");
            System.out.println("1. Додати нову задачу");
            System.out.println("2. Показати всі задачі");
            System.out.println("3. Видалити задачу"); // Нова опція
            System.out.println("4. Вийти");
            System.out.print("Ваш вибір: ");

            userInput = scanner.nextLine();

            switch (userInput) {
                case "1":
                    System.out.print("Введіть назву задачі: ");
                    String taskTitle = scanner.nextLine();
                    manager.addTask(taskTitle);
                    break;
                case "2":
                    System.out.println("\n--- Список задач ---");
                    manager.getAllTasks().forEach(System.out::println);
                    break;
                case "3":
                    try {
                        System.out.print("Введіть ID задачі для видалення: ");
                        long taskId = Long.parseLong(scanner.nextLine());
                        if (manager.removeTask(taskId)) {
                            System.out.println("Задача з ID " + taskId + " успішно видалена.");
                        } else {
                            System.out.println("Помилка: Задача з ID " + taskId + " не знайдена.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Помилка: ID повинен бути числом.");
                    }
                    break;
                case "4":
                    System.out.println("Завершення програми.");
                    break;
                default:
                    System.out.println("Некоректний вибір. Спробуйте ще раз.");
                    break;
            }
        } while (!userInput.equals("4"));

        scanner.close();
    }
}
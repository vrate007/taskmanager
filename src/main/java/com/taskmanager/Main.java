package com.taskmanager;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск програми 'Менеджер задач'...");

        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        String userInput;

        // Основний цикл програми
        do {
            System.out.println("\nОпції:");
            System.out.println("1. Додати нову задачу");
            System.out.println("2. Показати всі задачі");
            System.out.println("3. Вийти");
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
                    System.out.println("Завершення програми.");
                    break;
                default:
                    System.out.println("Некоректний вибір. Спробуйте ще раз.");
                    break;
            }
        } while (!userInput.equals("3"));

        scanner.close(); // Закрити сканер після завершення роботи
    }
}
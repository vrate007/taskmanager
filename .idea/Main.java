package com.taskmanager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск програми 'Менеджер задач'...");

        // Створюємо об'єкт для управління задачами
        TaskManager manager = new TaskManager();

        // Додаємо кілька задач
        manager.addTask("Вивчити Java");
        manager.addTask("Написати код");
        manager.addTask("Почитати книгу");

        System.out.println("\nУсі додані задачі:");
        manager.getAllTasks().forEach(System.out::println);
    }
}
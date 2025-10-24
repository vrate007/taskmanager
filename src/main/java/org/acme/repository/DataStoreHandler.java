package org.acme.repository;

import java.util.List;

/**
 * Інтерфейс для роботи з персистентним сховищем (Data Store).
 * Відповідає лише за операції введення/виведення (I/O).
 * (Save All, Load All)
 * @param <T> Тип об'єктів.
 */
public interface DataStoreHandler<T> {

    // R (Read): Завантажує всі дані
    List<T> loadAll();

    // C (Create) / U (Update): Зберігає весь список
    void saveAll(List<T> data);
}

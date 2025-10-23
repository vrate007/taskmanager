package org.acme;

import java.util.List;
import java.util.Optional;

public interface FileHandler<T> {
    List<T> load();
    void save(List<T> data);

    // U (Update): Оновлення
    void update(T item);

    // D (Delete): Видалення за ID
    void delete(long id); // Змінено на long

    // R (Read): Знайти за ID
    Optional<T> findById(long id); // Змінено на long
}
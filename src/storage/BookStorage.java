package com.bookmanager.storage;

import com.bookmanager.model.Book;

import java.io.IOException;
import java.util.List;

/**
 * Abstraction over how books are persisted. Allows the service layer
 * to remain independent of the concrete storage mechanism (file, database,
 * in-memory, etc.) — an example of the Strategy / Dependency Inversion pattern.
 */
public interface BookStorage {
    List<Book> loadAll() throws IOException;
    void saveAll(List<Book> books) throws IOException;
}

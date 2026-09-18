package com.bookmanager.storage;

import com.bookmanager.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * A non-persistent, in-memory implementation of BookStorage.
 * Used primarily for unit testing BookService without touching the filesystem,
 * and demonstrates that the service layer works against any BookStorage implementation.
 */
public class InMemoryBookStorage implements BookStorage {

    private List<Book> books = new ArrayList<>();

    @Override
    public List<Book> loadAll() {
        return new ArrayList<>(books);
    }

    @Override
    public void saveAll(List<Book> books) {
        this.books = new ArrayList<>(books);
    }
}

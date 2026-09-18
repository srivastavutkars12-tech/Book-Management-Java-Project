package com.bookmanager.service;

import com.bookmanager.exception.BookNotFoundException;
import com.bookmanager.exception.DuplicateBookException;
import com.bookmanager.exception.InvalidPageException;
import com.bookmanager.model.Book;
import com.bookmanager.model.ReadingStatus;
import com.bookmanager.storage.BookStorage;
import com.bookmanager.util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Core business-logic layer for the Book Management System.
 * Validates input, applies domain rules, and delegates persistence
 * to whichever BookStorage implementation it was constructed with.
 */
public class BookService {

    private final BookStorage storage;
    private final List<Book> books;

    public BookService(BookStorage storage) throws IOException {
        this.storage = storage;
        this.books = new ArrayList<>(storage.loadAll());
        Logger.info("BookService initialized with " + books.size() + " book(s) loaded.");
    }

    /** Adds a new book to the library. */
    public Book addBook(String title, String author, String isbn, String genre, int totalPages)
            throws DuplicateBookException, InvalidPageException {

        if (title == null || title.isBlank()) {
            throw new InvalidPageException("Book title cannot be empty.");
        }
        if (isbn == null || isbn.isBlank()) {
            throw new InvalidPageException("ISBN cannot be empty.");
        }
        if (totalPages <= 0) {
            throw new InvalidPageException("Total pages must be a positive number.");
        }
        if (findByIsbn(isbn).isPresent()) {
            throw new DuplicateBookException("A book with ISBN '" + isbn + "' already exists.");
        }

        Book book = new Book(title, author, isbn, genre, totalPages);
        books.add(book);
        persist();
        Logger.info("Added book: " + book.getIsbn() + " - " + book.getTitle());
        return book;
    }

    /** Updates the current reading page of a book and adjusts its status accordingly. */
    public Book updateProgress(String isbn, int newPage) throws BookNotFoundException, InvalidPageException {
        Book book = findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("No book found with ISBN '" + isbn + "'."));

        if (newPage < 0) {
            throw new InvalidPageException("Page number cannot be negative.");
        }
        if (newPage > book.getTotalPages()) {
            throw new InvalidPageException(
                    "Page " + newPage + " exceeds this book's total of " + book.getTotalPages() + " pages.");
        }

        book.setCurrentPage(newPage);
        if (newPage == 0) {
            book.setStatus(ReadingStatus.UNREAD);
        } else if (newPage == book.getTotalPages()) {
            book.setStatus(ReadingStatus.FINISHED);
        } else {
            book.setStatus(ReadingStatus.READING);
        }

        persist();
        Logger.info("Updated progress for " + isbn + " -> page " + newPage);
        return book;
    }

    /** Edits a book's descriptive metadata (title/author/genre). ISBN and progress are untouched. */
    public Book editDetails(String isbn, String newTitle, String newAuthor, String newGenre)
            throws BookNotFoundException {
        Book book = findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("No book found with ISBN '" + isbn + "'."));

        if (newTitle != null && !newTitle.isBlank()) book.setTitle(newTitle);
        if (newAuthor != null && !newAuthor.isBlank()) book.setAuthor(newAuthor);
        if (newGenre != null && !newGenre.isBlank()) book.setGenre(newGenre);

        persist();
        Logger.info("Edited details for " + isbn);
        return book;
    }

    /** Removes a book from the library. */
    public void deleteBook(String isbn) throws BookNotFoundException {
        Book book = findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("No book found with ISBN '" + isbn + "'."));
        books.remove(book);
        persist();
        Logger.info("Deleted book: " + isbn);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return books.stream().filter(b -> b.getIsbn().equalsIgnoreCase(isbn)).findFirst();
    }

    public List<Book> searchByTitle(String keyword) {
        String lower = keyword.toLowerCase();
        return books.stream().filter(b -> b.getTitle().toLowerCase().contains(lower)).toList();
    }

    public List<Book> searchByGenre(String genre) {
        return books.stream()
                .filter(b -> b.getGenre() != null && b.getGenre().equalsIgnoreCase(genre))
                .toList();
    }

    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }

    private void persist() {
        try {
            storage.saveAll(books);
        } catch (IOException e) {
            Logger.error("Failed to persist library data: " + e.getMessage());
        }
    }
}

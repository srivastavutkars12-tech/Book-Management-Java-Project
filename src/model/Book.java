package com.bookmanager.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a single book in the user's personal library.
 * Encapsulates identity fields (title, author, ISBN, genre) and
 * reading-progress fields (totalPages, currentPage, status).
 */
public class Book {

    private String title;
    private String author;
    private final String isbn;      // unique identifier for the book
    private String genre;
    private int totalPages;
    private int currentPage;
    private ReadingStatus status;
    private final LocalDate dateAdded;

    public Book(String title, String author, String isbn, String genre, int totalPages) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.totalPages = totalPages;
        this.currentPage = 0;
        this.status = ReadingStatus.UNREAD;
        this.dateAdded = LocalDate.now();
    }

    // Full constructor used when reconstructing a Book from storage
    public Book(String title, String author, String isbn, String genre,
                int totalPages, int currentPage, ReadingStatus status, LocalDate dateAdded) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.status = status;
        this.dateAdded = dateAdded;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public ReadingStatus getStatus() { return status; }
    public void setStatus(ReadingStatus status) { this.status = status; }

    public LocalDate getDateAdded() { return dateAdded; }

    /** Percentage of the book read so far, rounded to nearest whole percent. */
    public double getProgressPercent() {
        if (totalPages <= 0) return 0.0;
        return Math.round((currentPage * 10000.0) / totalPages) / 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return isbn.equalsIgnoreCase(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" by %s (%s) - %d/%d pages (%.1f%%) - %s",
                isbn, title, author, genre, currentPage, totalPages, getProgressPercent(), status);
    }
}

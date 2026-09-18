package com.bookmanager.service;

import com.bookmanager.model.Book;
import com.bookmanager.model.ReadingStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Reporting / analytics module. Computes summary statistics over the
 * library's books — this is functional module #3, distinct from the
 * CRUD-focused BookService.
 */
public class LibraryStatistics {

    private final List<Book> books;

    public LibraryStatistics(List<Book> books) {
        this.books = books;
    }

    public int totalBooks() {
        return books.size();
    }

    public long countByStatus(ReadingStatus status) {
        return books.stream().filter(b -> b.getStatus() == status).count();
    }

    public double averageProgressPercent() {
        return books.stream()
                .filter(b -> b.getStatus() != ReadingStatus.UNREAD)
                .mapToDouble(Book::getProgressPercent)
                .average()
                .orElse(0.0);
    }

    public Optional<Book> longestBook() {
        return books.stream().max(Comparator.comparingInt(Book::getTotalPages));
    }

    public Optional<Book> mostAdvancedBook() {
        return books.stream()
                .filter(b -> b.getStatus() == ReadingStatus.READING)
                .max(Comparator.comparingDouble(Book::getProgressPercent));
    }

    /** Builds a human-readable summary report of the whole library. */
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Library Statistics =====\n");
        sb.append(String.format("Total books:      %d%n", totalBooks()));
        sb.append(String.format("Unread:           %d%n", countByStatus(ReadingStatus.UNREAD)));
        sb.append(String.format("Currently reading:%d%n", countByStatus(ReadingStatus.READING)));
        sb.append(String.format("Finished:         %d%n", countByStatus(ReadingStatus.FINISHED)));
        sb.append(String.format("Avg. progress (started books): %.1f%%%n", averageProgressPercent()));
        longestBook().ifPresent(b ->
                sb.append(String.format("Longest book:     %s (%d pages)%n", b.getTitle(), b.getTotalPages())));
        mostAdvancedBook().ifPresent(b ->
                sb.append(String.format("Furthest along right now: %s (%.1f%%)%n", b.getTitle(), b.getProgressPercent())));
        sb.append("===============================");
        return sb.toString();
    }
}

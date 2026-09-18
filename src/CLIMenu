package com.bookmanager.ui;

import com.bookmanager.exception.BookNotFoundException;
import com.bookmanager.exception.DuplicateBookException;
import com.bookmanager.exception.InvalidPageException;
import com.bookmanager.model.Book;
import com.bookmanager.service.BookService;
import com.bookmanager.service.LibraryStatistics;
import com.bookmanager.storage.BackupManager;
import com.bookmanager.util.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface. Drives the interaction loop and delegates
 * all business logic to BookService, keeping presentation and logic separate.
 */
public class CLIMenu {

    private final BookService service;
    private final BackupManager backupManager;
    private final Scanner scanner;

    public CLIMenu(BookService service, BackupManager backupManager) {
        this.service = service;
        this.backupManager = backupManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=========================================");
        System.out.println("   Welcome to the Book Management System");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> viewAllBooks();
                    case "2" -> addBook();
                    case "3" -> updateProgress();
                    case "4" -> editDetails();
                    case "5" -> deleteBook();
                    case "6" -> searchBooks();
                    case "7" -> showStatistics();
                    case "8" -> backupData();
                    case "9" -> restoreData();
                    case "0" -> {
                        running = false;
                        System.out.println("Goodbye! Happy reading.");
                    }
                    default -> System.out.println("Invalid option. Please choose a number from the menu.");
                }
            } catch (Exception e) {
                // Catches any unexpected runtime issue so the CLI never crashes mid-session.
                System.out.println("Something went wrong: " + e.getMessage());
                Logger.error("Unexpected error in CLI: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. View all books");
        System.out.println("2. Add a new book");
        System.out.println("3. Update reading progress");
        System.out.println("4. Edit book details");
        System.out.println("5. Delete a book");
        System.out.println("6. Search books");
        System.out.println("7. View library statistics");
        System.out.println("8. Backup data");
        System.out.println("9. Restore from backup");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void viewAllBooks() {
        List<Book> books = service.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("Your library is empty. Add a book to get started!");
            return;
        }
        System.out.println("\n--- Your Library (" + books.size() + " book(s)) ---");
        books.forEach(System.out::println);
    }

    private void addBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();
        System.out.print("Total pages: ");
        int totalPages = readInt();

        try {
            Book book = service.addBook(title, author, isbn, genre, totalPages);
            System.out.println("Added: " + book);
        } catch (DuplicateBookException | InvalidPageException e) {
            System.out.println("Could not add book: " + e.getMessage());
        }
    }

    private void updateProgress() {
        System.out.print("ISBN of book to update: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("New current page: ");
        int page = readInt();

        try {
            Book book = service.updateProgress(isbn, page);
            System.out.println("Updated: " + book);
        } catch (BookNotFoundException | InvalidPageException e) {
            System.out.println("Could not update progress: " + e.getMessage());
        }
    }

    private void editDetails() {
        System.out.print("ISBN of book to edit: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("New title (leave blank to keep unchanged): ");
        String title = scanner.nextLine().trim();
        System.out.print("New author (leave blank to keep unchanged): ");
        String author = scanner.nextLine().trim();
        System.out.print("New genre (leave blank to keep unchanged): ");
        String genre = scanner.nextLine().trim();

        try {
            Book book = service.editDetails(isbn, title, author, genre);
            System.out.println("Updated: " + book);
        } catch (BookNotFoundException e) {
            System.out.println("Could not edit book: " + e.getMessage());
        }
    }

    private void deleteBook() {
        System.out.print("ISBN of book to delete: ");
        String isbn = scanner.nextLine().trim();
        try {
            service.deleteBook(isbn);
            System.out.println("Book deleted.");
        } catch (BookNotFoundException e) {
            System.out.println("Could not delete book: " + e.getMessage());
        }
    }

    private void searchBooks() {
        System.out.print("Search by (1) title or (2) genre: ");
        String mode = scanner.nextLine().trim();
        System.out.print("Enter search term: ");
        String term = scanner.nextLine().trim();

        List<Book> results = mode.equals("2") ? service.searchByGenre(term) : service.searchByTitle(term);
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void showStatistics() {
        LibraryStatistics stats = new LibraryStatistics(service.getAllBooks());
        System.out.println("\n" + stats.generateReport());
    }

    private void backupData() {
        try {
            backupManager.backup();
            System.out.println("Backup created successfully.");
        } catch (IOException e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    private void restoreData() {
        try {
            backupManager.restore();
            System.out.println("Data restored from backup. Restart the application to reload it.");
        } catch (IOException e) {
            System.out.println("Restore failed: " + e.getMessage());
        }
    }

    private int readInt() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid whole number: ");
            }
        }
    }
}

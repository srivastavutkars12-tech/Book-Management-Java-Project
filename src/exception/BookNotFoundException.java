package com.bookmanager.exception;

/**
 * Thrown when an operation references a book (by ISBN or title)
 * that does not exist in the library.
 */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}

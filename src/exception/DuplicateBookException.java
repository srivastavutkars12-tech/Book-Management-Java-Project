package com.bookmanager.exception;

/**
 * Thrown when attempting to add a book whose ISBN already exists in the library.
 */
public class DuplicateBookException extends Exception {
    public DuplicateBookException(String message) {
        super(message);
    }
}

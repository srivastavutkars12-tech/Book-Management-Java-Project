package com.bookmanager.exception;

/**
 * Thrown when a page number is invalid: negative, or greater than
 * the book's total page count, or the total page count itself is not positive.
 */
public class InvalidPageException extends Exception {
    public InvalidPageException(String message) {
        super(message);
    }
}

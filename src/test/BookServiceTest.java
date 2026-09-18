package com.bookmanager.test;

import com.bookmanager.exception.BookNotFoundException;
import com.bookmanager.exception.DuplicateBookException;
import com.bookmanager.exception.InvalidPageException;
import com.bookmanager.model.Book;
import com.bookmanager.model.ReadingStatus;
import com.bookmanager.service.BookService;
import com.bookmanager.service.LibraryStatistics;
import com.bookmanager.storage.InMemoryBookStorage;

import java.util.List;

/**
 * Lightweight, dependency-free test runner for BookService and LibraryStatistics.
 *
 * The project is built with plain javac/java (no build tool / no network access
 * to a Maven repository), so this hand-rolled runner plays the role JUnit would:
 * each test method returns a pass/fail result and a message. To run: 
 *   java com.bookmanager.test.BookServiceTest
 *
 * (If your environment does have Maven + JUnit available, these test bodies
 * can be ported directly into @Test methods with assertEquals/assertThrows.)
 */
public class BookServiceTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        testAddBook();
        testDuplicateIsbnRejected();
        testInvalidTotalPagesRejected();
        testUpdateProgressMarksFinished();
        testUpdateProgressRejectsOverflow();
        testDeleteBook();
        testBookNotFoundOnUpdate();
        testSearchByTitle();
        testStatisticsCounts();

        System.out.println("\n=====================================");
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
        System.out.println("=====================================");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testAddBook() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            Book b = service.addBook("Clean Code", "Robert Martin", "111", "Programming", 464);
            check("addBook stores the book", service.findByIsbn("111").isPresent());
            check("addBook returns correct title", b.getTitle().equals("Clean Code"));
            check("new book starts UNREAD", b.getStatus() == ReadingStatus.UNREAD);
        } catch (Exception e) {
            fail("testAddBook threw unexpected exception: " + e);
        }
    }

    private static void testDuplicateIsbnRejected() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            service.addBook("Book A", "Author A", "222", "Fiction", 200);
            try {
                service.addBook("Book B", "Author B", "222", "Fiction", 150);
                fail("Expected DuplicateBookException was not thrown");
            } catch (DuplicateBookException expected) {
                pass("Duplicate ISBN correctly rejected");
            }
        } catch (Exception e) {
            fail("testDuplicateIsbnRejected threw unexpected exception: " + e);
        }
    }

    private static void testInvalidTotalPagesRejected() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            try {
                service.addBook("Bad Book", "Someone", "333", "Fiction", 0);
                fail("Expected InvalidPageException for zero total pages");
            } catch (InvalidPageException expected) {
                pass("Zero total pages correctly rejected");
            }
        } catch (Exception e) {
            fail("testInvalidTotalPagesRejected threw unexpected exception: " + e);
        }
    }

    private static void testUpdateProgressMarksFinished() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            service.addBook("Short Story", "Author", "444", "Fiction", 100);
            Book updated = service.updateProgress("444", 100);
            check("book marked FINISHED at last page", updated.getStatus() == ReadingStatus.FINISHED);

            Book midway = service.updateProgress("444", 50);
            check("book marked READING mid-progress", midway.getStatus() == ReadingStatus.READING);
        } catch (Exception e) {
            fail("testUpdateProgressMarksFinished threw unexpected exception: " + e);
        }
    }

    private static void testUpdateProgressRejectsOverflow() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            service.addBook("Novel", "Author", "555", "Fiction", 300);
            try {
                service.updateProgress("555", 500);
                fail("Expected InvalidPageException for page beyond total");
            } catch (InvalidPageException expected) {
                pass("Overflow page number correctly rejected");
            }
        } catch (Exception e) {
            fail("testUpdateProgressRejectsOverflow threw unexpected exception: " + e);
        }
    }

    private static void testDeleteBook() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            service.addBook("To Delete", "Author", "666", "Fiction", 120);
            service.deleteBook("666");
            check("book removed after delete", service.findByIsbn("666").isEmpty());
        } catch (Exception e) {
            fail("testDeleteBook threw unexpected exception: " + e);
        }
    }

    private static void testBookNotFoundOnUpdate() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            try {
                service.updateProgress("does-not-exist", 10);
                fail("Expected BookNotFoundException");
            } catch (BookNotFoundException expected) {
                pass("Missing ISBN correctly raises BookNotFoundException");
            }
        } catch (Exception e) {
            fail("testBookNotFoundOnUpdate threw unexpected exception: " + e);
        }
    }

    private static void testSearchByTitle() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            service.addBook("The Hobbit", "J.R.R. Tolkien", "777", "Fantasy", 310);
            service.addBook("The Silmarillion", "J.R.R. Tolkien", "778", "Fantasy", 365);
            service.addBook("Dune", "Frank Herbert", "779", "Sci-Fi", 412);

            List<Book> results = service.searchByTitle("the");
            check("search finds both 'The...' titles", results.size() == 2);
        } catch (Exception e) {
            fail("testSearchByTitle threw unexpected exception: " + e);
        }
    }

    private static void testStatisticsCounts() {
        try {
            BookService service = new BookService(new InMemoryBookStorage());
            service.addBook("Book 1", "Author", "801", "Genre", 100);
            service.addBook("Book 2", "Author", "802", "Genre", 100);
            service.addBook("Book 3", "Author", "803", "Genre", 100);
            service.updateProgress("802", 100); // finished
            service.updateProgress("803", 50);  // reading

            LibraryStatistics stats = new LibraryStatistics(service.getAllBooks());
            check("total books = 3", stats.totalBooks() == 3);
            check("1 unread", stats.countByStatus(ReadingStatus.UNREAD) == 1);
            check("1 finished", stats.countByStatus(ReadingStatus.FINISHED) == 1);
            check("1 reading", stats.countByStatus(ReadingStatus.READING) == 1);
        } catch (Exception e) {
            fail("testStatisticsCounts threw unexpected exception: " + e);
        }
    }

    // --- tiny assertion helpers ---

    private static void check(String description, boolean condition) {
        if (condition) {
            pass(description);
        } else {
            fail(description);
        }
    }

    private static void pass(String description) {
        passed++;
        System.out.println("[PASS] " + description);
    }

    private static void fail(String description) {
        failed++;
        System.out.println("[FAIL] " + description);
    }
}

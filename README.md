# Book Management System

A Java command-line application for managing a personal reading library —
track books, monitor reading progress, search your collection, view
statistics, and back up your data — built with a layered, testable
architecture.

## Overview

The Book Management System lets a reader maintain a digital library of the
books they own or are reading. Each book tracks its title, author, genre,
ISBN, total page count, and current page, and its status (Unread / Reading /
Finished) is derived automatically from reading progress. All data is
persisted to a JSON file between sessions, with one-command backup and
restore.

## Features

- **Add, edit, and delete books** with full input validation
- **Track reading progress** by page number; status updates automatically
  (Unread → Reading → Finished)
- **Search** by title (partial match) or by genre
- **Library statistics**: totals by status, average progress, longest book,
  book you're furthest along in
- **Backup & restore** the entire library with a single menu option
- **Persistent storage** to `data.json`, human-readable and portable
- **File-based logging** (`app.log`) of all mutating operations and errors
- **Custom exceptions** for invalid input, duplicate ISBNs, and missing books
- Clean layered architecture: model / storage / service / UI, so the
  business logic has zero dependency on the console interface

## Technologies & Tools Used

- **Language**: Java 21
- **Data storage**: JSON file (hand-rolled reader/writer, no external
  dependency required — the project builds with plain `javac`/`java`)
- **Testing**: self-contained assertion-based test runner
  (`com.bookmanager.test.BookServiceTest`) covering the service layer
- **Design patterns**: Strategy (the `BookStorage` interface, implemented by
  both `FileBookStorage` and `InMemoryBookStorage`), layered
  architecture (UI → Service → Storage)

## Project Structure

```
BookManager/
├── src/com/bookmanager/
│   ├── Main.java                     # Entry point — wires everything together
│   ├── model/
│   │   ├── Book.java                 # Core domain model
│   │   └── ReadingStatus.java        # Unread / Reading / Finished enum
│   ├── exception/
│   │   ├── BookNotFoundException.java
│   │   ├── DuplicateBookException.java
│   │   └── InvalidPageException.java
│   ├── storage/
│   │   ├── BookStorage.java          # Persistence interface
│   │   ├── FileBookStorage.java      # JSON file implementation
│   │   ├── InMemoryBookStorage.java  # In-memory implementation (used in tests)
│   │   └── BackupManager.java        # Backup / restore logic
│   ├── service/
│   │   ├── BookService.java          # CRUD + validation (module 1 & 2)
│   │   └── LibraryStatistics.java    # Reporting / analytics (module 3)
│   ├── ui/
│   │   └── CLIMenu.java              # Console interaction loop
│   ├── util/
│   │   └── Logger.java               # File-based logging utility
│   └── test/
│       └── BookServiceTest.java      # Unit/validation tests
├── README.md
├── statement.md
└── architecture.md
```

## Installation & Setup

### Prerequisites

- JDK 17 or higher (developed and tested on JDK 21)

### Steps to Install & Run

1. **Clone the repository**
   ```
   git clone <repository-url>
   cd BookManager
   ```

2. **Compile**
   ```
   javac -d bin $(find src -name "*.java")
   ```

3. **Run the application**
   ```
   java -cp bin com.bookmanager.Main
   ```

4. **Follow the on-screen menu** — choose a number 0–9 for each action.

## Instructions for Testing

Run the built-in test suite (no external test framework required):

```
java -cp bin com.bookmanager.test.BookServiceTest
```

This exercises `BookService` and `LibraryStatistics` against an in-memory
storage backend and prints a `[PASS]`/`[FAIL]` line per case, plus a summary.
All 15 cases should pass, covering: adding books, rejecting duplicate ISBNs,
rejecting invalid page counts, progress updates that change status
(Unread → Reading → Finished), rejecting out-of-range pages, deleting books,
missing-book errors, title search, and statistics counts.

### Manual testing scenarios

1. Add a book, then view all books — confirm it appears with 0% progress.
2. Update its progress to a page beyond the total — confirm it's rejected.
3. Update its progress to the last page — confirm status becomes `FINISHED`.
4. Add a second book with the same ISBN — confirm it's rejected as a duplicate.
5. Run `Backup data`, delete a book, then `Restore from backup`, restart the
   app — confirm the deleted book is back.
6. Check `app.log` after a session — confirm timestamped entries exist for
   each action.

## Data Format

```json
[
  {
    "title": "Harry Potter",
    "author": "J.K. Rowling",
    "isbn": "001",
    "genre": "Fantasy",
    "totalPages": 320,
    "currentPage": 150,
    "status": "READING",
    "dateAdded": "2026-09-16"
  }
]
```

## Known Limitations

- Single-user, single-machine (no accounts, no concurrent access)
- Console interface only (no GUI)
- No fractional/time-based reading-speed estimation

## Future Enhancements

- Swing/JavaFX GUI or a simple web front end
- Multi-user accounts with per-user libraries
- Book ratings, reviews, and reading-history timeline
- Migration from file storage to an embedded database (SQLite) via JDBC
- Genre-based recommendations

## Author

Student Project — VITyarthi "Build Your Own Project" Initiative

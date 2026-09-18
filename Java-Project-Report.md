# Project Report: Book Management System (Java-Project)

**Repository:** https://github.com/srivastavutkars12-tech/Book-Management-Java-Project
**Author:** utkarsh25bai11376 — Student Project, VITyarthi "Build Your Own Project" Initiative
**Language:** Java (JDK 21, minimum JDK 17)

---

## 1. Introduction

The **Book Management System** is a Java command-line application that lets a single user maintain a personal digital library. It allows the user to add, edit, and remove books, track reading progress page-by-page, search and filter the collection, view aggregate statistics, and back up or restore the entire library — all without requiring a database, an internet connection, or any external dependency.

## 2. Problem Statement

Readers who juggle multiple books at once — physical copies, e-books, borrowed titles — often lose track of where they left off, or which books they have already finished. The project addresses this by providing a simple, dependable, local tool to record a book's identity and reading progress without the overhead of an account, cloud storage, or a heavyweight application.

## 3. Scope

### In Scope
- Recording a book's title, author, genre, ISBN, and total page count
- Tracking and updating current page read, with status (Unread / Reading / Finished) derived automatically
- Searching the library by title (partial match) or genre
- Viewing aggregate library statistics (counts by status, average progress, longest book, furthest-along book)
- Persisting the library to disk between sessions, with one-command backup and restore

### Out of Scope
- Multi-user support
- Graphical user interface
- Networked or cloud storage
- Integration with external book databases (e.g., ISBN lookup APIs)

These excluded items are listed as future enhancements (Section 8).

## 4. Target Users

- Individual readers wanting a lightweight, local way to track their personal reading list and progress
- Students/self-learners seeking a reference implementation of a layered Java console application (model / storage / service / UI) with input validation, custom exceptions, and unit tests

## 5. Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | Add / Edit / Delete Books | Full CRUD operations with input validation |
| 2 | Reading Progress Tracking | Update current page; status auto-transitions Unread → Reading → Finished |
| 3 | Search | By title (partial match) or by genre |
| 4 | Library Statistics | Totals by status, average progress, longest book, furthest-along book |
| 5 | Backup & Restore | Entire library backed up/restored via a single menu option |
| 6 | Persistent Storage | Human-readable, portable `data.json` file |
| 7 | Logging | File-based logging (`app.log`) of all mutating operations and errors |
| 8 | Custom Exceptions | Dedicated exceptions for invalid input, duplicate ISBNs, and missing books |

## 6. Technologies & Design

- **Language:** Java 21 (builds and runs with plain `javac` / `java` — no external dependencies)
- **Data Storage:** Hand-rolled JSON reader/writer, persisted to `data.json`
- **Testing:** Self-contained, assertion-based test runner (`com.bookmanager.test.BookServiceTest`) — no external test framework required
- **Design Patterns:**
  - **Strategy Pattern** — the `BookStorage` interface is implemented by both `FileBookStorage` (production) and `InMemoryBookStorage` (testing)
  - **Layered Architecture** — UI → Service → Storage, keeping business logic independent of the console interface

## 7. Project Structure

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
│   │   ├── BookService.java          # CRUD + validation
│   │   └── LibraryStatistics.java    # Reporting / analytics
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

The layered structure — **model → storage → service → UI** — cleanly separates domain data, persistence, business logic, and presentation, keeping the core logic testable independent of the console interface.

## 8. Installation & Setup

### Prerequisites
- JDK 17 or higher (developed and tested on JDK 21)

### Steps

```bash
# 1. Clone the repository
git clone <repository-url>
cd BookManager

# 2. Compile
javac -d bin $(find src -name "*.java")

# 3. Run the application
java -cp bin com.bookmanager.Main
```

Once running, the user selects a menu option (0–9) to add, edit, search, view statistics, back up, or restore books.

## 9. Testing

The project ships a self-contained assertion-based test suite requiring no external framework:

```bash
java -cp bin com.bookmanager.test.BookServiceTest
```

This runs `BookService` and `LibraryStatistics` against an in-memory storage backend, printing `[PASS]`/`[FAIL]` per case plus a summary. All **15 test cases** should pass, covering:
- Adding books
- Rejecting duplicate ISBNs
- Rejecting invalid/out-of-range page counts
- Progress updates that trigger status transitions (Unread → Reading → Finished)
- Deleting books and handling missing-book errors
- Title search
- Statistics counts

### Manual Testing Scenarios
1. Add a book, view all books — confirm it appears at 0% progress
2. Update progress beyond total pages — confirm rejection
3. Update progress to the last page — confirm status becomes `FINISHED`
4. Add a second book with a duplicate ISBN — confirm rejection
5. Run backup, delete a book, restore from backup, restart — confirm the book reappears
6. Inspect `app.log` after a session — confirm timestamped entries for each action

## 10. Data Format

Library data is stored as a JSON array in `data.json`:

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

## 11. Known Limitations

- Single-user, single-machine — no accounts or concurrent access
- Console interface only (no GUI)
- No fractional or time-based reading-speed estimation

## 12. Future Enhancements

- Swing/JavaFX GUI or a simple web front end
- Multi-user accounts with per-user libraries
- Book ratings, reviews, and a reading-history timeline
- Migration from file storage to an embedded database (SQLite) via JDBC
- Genre-based recommendations

## 13. Conclusion

The Book Management System demonstrates a well-structured, dependency-free Java console application built around clean layered architecture, custom exception handling, and a self-contained test suite. Its use of the Strategy pattern for storage (`FileBookStorage` vs. `InMemoryBookStorage`) makes the service layer independently testable, and its plain-JSON persistence keeps the project portable and easy to run with nothing beyond a standard JDK. It serves both as a practical local reading tracker and as a reference implementation of good separation of concerns in a small Java project.

---
*Report generated from repository contents (README.md and statement.md) as of September 2026.*

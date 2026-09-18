# Problem Statement

Avid readers often juggle several books at once — physical copies,
e-books, borrowed titles — and lose track of where they left off in each
one, or which books they've already finished. A simple, dependable way to
record a book's identity and reading progress, without needing an account,
an internet connection, or a heavyweight app, solves this cleanly.

## Scope of the Project

This project delivers a Java command-line application that lets a single
user maintain a personal library of books. It covers:

- Recording a book's title, author, genre, ISBN, and total page count
- Tracking and updating the current page read, with the book's status
  (Unread / Reading / Finished) derived automatically
- Searching the library by title or genre
- Viewing aggregate statistics about the library (counts by status,
  average progress, longest book, etc.)
- Persisting the library to disk between runs, with backup and restore

Out of scope: multi-user support, a graphical interface, networked/cloud
storage, and integration with external book databases (e.g. ISBN lookup
APIs) — these are listed as future enhancements.

## Target Users

- Individual readers who want a lightweight, local way to track their
  personal reading list and progress
- Students/self-learners looking for a reference implementation of a
  layered Java console application (model / storage / service / UI) with
  validation, custom exceptions, and unit tests

## High-Level Features

1. **Library management** — add, edit, delete, and list books (CRUD)
2. **Reading progress tracking** — update current page; status transitions
   automatically between Unread, Reading, and Finished
3. **Reporting & analytics** — a statistics module summarizing the state of
   the whole library
4. **Data persistence & recovery** — JSON file storage with one-command
   backup and restore
5. **Validation & error handling** — custom checked exceptions for
   duplicate ISBNs, invalid page numbers, and missing books, so the CLI
   never crashes on bad input

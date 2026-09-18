package com.bookmanager.storage;

import com.bookmanager.model.Book;
import com.bookmanager.model.ReadingStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Persists the book library to a JSON file on disk.
 *
 * This is a hand-rolled, dependency-free JSON reader/writer scoped to the
 * fixed Book schema used by this application (no external JSON library
 * required, keeping the project runnable with just javac/java).
 */
public class FileBookStorage implements BookStorage {

    private final Path dataFile;

    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{[^{}]*}");
    private static final Pattern FIELD_PATTERN = Pattern.compile("\"(\\w+)\"\\s*:\\s*(\"([^\"]*)\"|\\d+)");

    public FileBookStorage(String filePath) {
        this.dataFile = Path.of(filePath);
    }

    @Override
    public List<Book> loadAll() throws IOException {
        List<Book> books = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return books; // No data yet — treat as an empty library.
        }

        String content = Files.readString(dataFile, StandardCharsets.UTF_8).trim();
        if (content.isEmpty() || content.equals("[]")) {
            return books;
        }

        Matcher objectMatcher = OBJECT_PATTERN.matcher(content);
        while (objectMatcher.find()) {
            String obj = objectMatcher.group();
            String title = null, author = null, isbn = null, genre = null, statusStr = null, dateStr = null;
            int totalPages = 0, currentPage = 0;

            Matcher fieldMatcher = FIELD_PATTERN.matcher(obj);
            while (fieldMatcher.find()) {
                String key = fieldMatcher.group(1);
                String strVal = fieldMatcher.group(3);
                String rawVal = fieldMatcher.group(2);

                switch (key) {
                    case "title" -> title = strVal;
                    case "author" -> author = strVal;
                    case "isbn" -> isbn = strVal;
                    case "genre" -> genre = strVal;
                    case "status" -> statusStr = strVal;
                    case "dateAdded" -> dateStr = strVal;
                    case "totalPages" -> totalPages = Integer.parseInt(rawVal);
                    case "currentPage" -> currentPage = Integer.parseInt(rawVal);
                    default -> { /* ignore unknown fields for forward compatibility */ }
                }
            }

            if (title != null && isbn != null) {
                ReadingStatus status = statusStr != null ? ReadingStatus.valueOf(statusStr) : ReadingStatus.UNREAD;
                LocalDate dateAdded = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
                books.add(new Book(title, author, isbn, genre, totalPages, currentPage, status, dateAdded));
            }
        }
        return books;
    }

    @Override
    public void saveAll(List<Book> books) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            sb.append("  {\n");
            sb.append("    \"title\": \"").append(escape(b.getTitle())).append("\",\n");
            sb.append("    \"author\": \"").append(escape(b.getAuthor())).append("\",\n");
            sb.append("    \"isbn\": \"").append(escape(b.getIsbn())).append("\",\n");
            sb.append("    \"genre\": \"").append(escape(b.getGenre())).append("\",\n");
            sb.append("    \"totalPages\": ").append(b.getTotalPages()).append(",\n");
            sb.append("    \"currentPage\": ").append(b.getCurrentPage()).append(",\n");
            sb.append("    \"status\": \"").append(b.getStatus().name()).append("\",\n");
            sb.append("    \"dateAdded\": \"").append(b.getDateAdded().toString()).append("\"\n");
            sb.append("  }").append(i < books.size() - 1 ? ",\n" : "\n");
        }
        sb.append("]\n");

        // Write atomically via a temp file to reduce risk of corruption on crash.
        Path tempFile = Path.of(dataFile.toString() + ".tmp");
        Files.writeString(tempFile, sb.toString(), StandardCharsets.UTF_8);
        Files.move(tempFile, dataFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public Path getDataFile() {
        return dataFile;
    }
}

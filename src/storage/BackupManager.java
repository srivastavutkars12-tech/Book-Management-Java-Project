package com.bookmanager.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Handles creating and restoring backups of the data file.
 * Supports the "Reliability" non-functional requirement: users can
 * recover their library if the main data file is lost or corrupted.
 */
public class BackupManager {

    private final Path dataFile;
    private final Path backupFile;

    public BackupManager(Path dataFile, Path backupFile) {
        this.dataFile = dataFile;
        this.backupFile = backupFile;
    }

    /** Copies the current data file to the backup location. */
    public void backup() throws IOException {
        if (!Files.exists(dataFile)) {
            throw new IOException("No data file exists yet — nothing to back up.");
        }
        Files.copy(dataFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /** Restores the data file from the backup location, overwriting current data. */
    public void restore() throws IOException {
        if (!Files.exists(backupFile)) {
            throw new IOException("No backup file found to restore from.");
        }
        Files.copy(backupFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public boolean backupExists() {
        return Files.exists(backupFile);
    }
}

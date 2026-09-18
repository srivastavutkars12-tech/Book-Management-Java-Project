package com.bookmanager;

import com.bookmanager.service.BookService;
import com.bookmanager.storage.BackupManager;
import com.bookmanager.storage.FileBookStorage;
import com.bookmanager.ui.CLIMenu;
import com.bookmanager.util.Logger;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Application entry point. Wires together the storage, service, and UI layers.
 */
public class Main {
    public static void main(String[] args) {
        try {
            FileBookStorage storage = new FileBookStorage("data.json");
            BookService service = new BookService(storage);
            BackupManager backupManager = new BackupManager(
                    storage.getDataFile(), Path.of("data_backup.json"));

            CLIMenu menu = new CLIMenu(service, backupManager);
            menu.start();
        } catch (IOException e) {
            Logger.error("Fatal error starting application: " + e.getMessage());
            System.err.println("Could not start the Book Management System: " + e.getMessage());
        }
    }
}

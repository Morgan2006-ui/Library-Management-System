package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * DataPersistence - Handles all file I/O operations for the library system
 * Provides robust data persistence with comprehensive exception handling,
 * automatic backups, and data integrity validation.
 * 
 * Features:
 * - JSON-based data storage for human readability
 * - Automatic backup creation before each save
 * - Data integrity validation
 * - Comprehensive exception handling with detailed error messages
 * - Configurable storage location
 * 
 * @author Group Leader
 * @version 1.0
 */
public class DataPersistence {
    
    // File paths
    private static final String DATA_DIR = "library_data";
    private static final String DATA_FILE = "library_data.json";
    private static final String BACKUP_DIR = "backups";
    
    // Full paths
    private final Path dataDirectory;
    private final Path dataFilePath;
    private final Path backupDirectory;
    
    // JSON serializer
    private final Gson gson;
    
    // Configuration
    private static final int MAX_BACKUPS = 5;
    private static final boolean AUTO_BACKUP = true;
    
    /**
     * Constructor - Initializes the persistence layer
     * Creates necessary directories and configures JSON serializer
     */
    public DataPersistence() {
        // Initialize paths
        dataDirectory = Paths.get(DATA_DIR);
        dataFilePath = dataDirectory.resolve(DATA_FILE);
        backupDirectory = dataDirectory.resolve(BACKUP_DIR);
        
        // Configure Gson for pretty printing and proper serialization
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();
        
        // Initialize directories
        try {
            initializeDirectories();
        } catch (IOException e) {
            System.err.println("Warning: Could not initialize data directories: " + e.getMessage());
        }
    }
    
    /**
     * Initialize required directories for data storage
     * Creates directories if they don't exist
     * 
     * @throws IOException if directory creation fails
     */
    private void initializeDirectories() throws IOException {
        if (!Files.exists(dataDirectory)) {
            Files.createDirectories(dataDirectory);
            System.out.println("Created data directory: " + dataDirectory);
        }
        
        if (!Files.exists(backupDirectory)) {
            Files.createDirectories(backupDirectory);
            System.out.println("Created backup directory: " + backupDirectory);
        }
    }
    
    /**
     * Save data to persistent storage
     * Automatically creates a backup before saving if AUTO_BACKUP is enabled
     * 
     * @param data Map containing all library data
     * @throws PersistenceException if save operation fails
     */
    public void saveData(Map<String, Object> data) throws PersistenceException {
        if (data == null) {
            throw new PersistenceException("Cannot save null data");
        }
        
        try {
            // Create backup before saving
            if (AUTO_BACKUP && Files.exists(dataFilePath)) {
                createBackup();
            }
            
            // Serialize data to JSON
            String jsonData = gson.toJson(data);
            
            // Write to temporary file first
            Path tempFile = dataDirectory.resolve(DATA_FILE + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(jsonData);
            }
            
            // Atomic move to actual file
            Files.move(tempFile, dataFilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            
            System.out.println("Data saved successfully to: " + dataFilePath);
            
            // Clean up old backups
            cleanupOldBackups();
            
        } catch (IOException e) {
            throw new PersistenceException("Failed to save data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new PersistenceException("Unexpected error during save: " + e.getMessage(), e);
        }
    }
    
    /**
     * Load data from persistent storage
     * 
     * @return Map containing all library data, or empty map if file doesn't exist
     * @throws PersistenceException if load operation fails
     */
    public Map<String, Object> loadData() throws PersistenceException {
        // Return empty map if file doesn't exist
        if (!Files.exists(dataFilePath)) {
            System.out.println("No existing data file found. Starting with empty data.");
            return new HashMap<>();
        }
        
        try {
            // Read JSON data from file
            String jsonData = Files.readString(dataFilePath);
            
            // Check if file is empty
            if (jsonData == null || jsonData.trim().isEmpty()) {
                System.out.println("Data file is empty. Starting with empty data.");
                return new HashMap<>();
            }
            
            // Deserialize JSON to Map
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(jsonData, type);
            
            // Validate loaded data
            if (data == null) {
                System.out.println("Loaded data is null. Starting with empty data.");
                return new HashMap<>();
            }
            
            System.out.println("Data loaded successfully from: " + dataFilePath);
            return data;
            
        } catch (IOException e) {
            throw new PersistenceException("Failed to read data file: " + e.getMessage(), e);
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new PersistenceException("Data file is corrupted or invalid JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new PersistenceException("Unexpected error during load: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create a backup of the current data file
     * Backup filename includes timestamp for easy identification
     * 
     * @throws IOException if backup creation fails
     */
    private void createBackup() throws IOException {
        if (!Files.exists(dataFilePath)) {
            return; // Nothing to backup
        }
        
        try {
            // Generate backup filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFilename = "library_data_" + timestamp + ".json";
            Path backupPath = backupDirectory.resolve(backupFilename);
            
            // Copy current data file to backup
            Files.copy(dataFilePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Backup created: " + backupFilename);
            
        } catch (IOException e) {
            System.err.println("Warning: Failed to create backup: " + e.getMessage());
            // Don't throw exception - backup failure shouldn't prevent saving
        }
    }
    
    /**
     * Clean up old backup files, keeping only the most recent MAX_BACKUPS files
     * 
     * @throws IOException if cleanup fails
     */
    private void cleanupOldBackups() throws IOException {
        try {
            // Get all backup files
            List<Path> backupFiles = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDirectory, "library_data_*.json")) {
                for (Path entry : stream) {
                    backupFiles.add(entry);
                }
            }
            
            // Sort by last modified time (newest first)
            backupFiles.sort((p1, p2) -> {
                try {
                    return Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1));
                } catch (IOException e) {
                    return 0;
                }
            });
            
            // Delete old backups beyond MAX_BACKUPS
            if (backupFiles.size() > MAX_BACKUPS) {
                for (int i = MAX_BACKUPS; i < backupFiles.size(); i++) {
                    Files.deleteIfExists(backupFiles.get(i));
                    System.out.println("Deleted old backup: " + backupFiles.get(i).getFileName());
                }
            }
            
        } catch (IOException e) {
            System.err.println("Warning: Failed to cleanup old backups: " + e.getMessage());
            // Don't throw exception - cleanup failure shouldn't prevent saving
        }
    }
    
    /**
     * Restore data from a specific backup file
     * 
     * @param backupFilename The name of the backup file to restore
     * @return Map containing the restored data
     * @throws PersistenceException if restore operation fails
     */
    public Map<String, Object> restoreFromBackup(String backupFilename) throws PersistenceException {
        Path backupPath = backupDirectory.resolve(backupFilename);
        
        if (!Files.exists(backupPath)) {
            throw new PersistenceException("Backup file not found: " + backupFilename);
        }
        
        try {
            // Read backup file
            String jsonData = Files.readString(backupPath);
            
            // Deserialize JSON to Map
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(jsonData, type);
            
            System.out.println("Data restored from backup: " + backupFilename);
            return data;
            
        } catch (IOException e) {
            throw new PersistenceException("Failed to restore from backup: " + e.getMessage(), e);
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new PersistenceException("Backup file is corrupted: " + e.getMessage(), e);
        }
    }
    
    /**
     * List all available backup files
     * 
     * @return List of backup filenames, sorted by date (newest first)
     * @throws PersistenceException if listing fails
     */
    public List<String> listBackups() throws PersistenceException {
        List<String> backupFiles = new ArrayList<>();
        
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDirectory, "library_data_*.json")) {
                for (Path entry : stream) {
                    backupFiles.add(entry.getFileName().toString());
                }
            }
            
            // Sort by filename (which includes timestamp)
            backupFiles.sort(Collections.reverseOrder());
            
            return backupFiles;
            
        } catch (IOException e) {
            throw new PersistenceException("Failed to list backups: " + e.getMessage(), e);
        }
    }
    
    /**
     * Export data to a custom location
     * 
     * @param data The data to export
     * @param exportPath The path where to export the data
     * @throws PersistenceException if export fails
     */
    public void exportData(Map<String, Object> data, Path exportPath) throws PersistenceException {
        try {
            String jsonData = gson.toJson(data);
            Files.writeString(exportPath, jsonData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Data exported to: " + exportPath);
        } catch (IOException e) {
            throw new PersistenceException("Failed to export data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Import data from a custom location
     * 
     * @param importPath The path to import data from
     * @return Map containing the imported data
     * @throws PersistenceException if import fails
     */
    public Map<String, Object> importData(Path importPath) throws PersistenceException {
        if (!Files.exists(importPath)) {
            throw new PersistenceException("Import file not found: " + importPath);
        }
        
        try {
            String jsonData = Files.readString(importPath);
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(jsonData, type);
            System.out.println("Data imported from: " + importPath);
            return data;
        } catch (IOException e) {
            throw new PersistenceException("Failed to import data: " + e.getMessage(), e);
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new PersistenceException("Import file is corrupted: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if data file exists
     * 
     * @return true if data file exists, false otherwise
     */
    public boolean dataFileExists() {
        return Files.exists(dataFilePath);
    }
    
    /**
     * Get the size of the data file in bytes
     * 
     * @return Size in bytes, or -1 if file doesn't exist
     */
    public long getDataFileSize() {
        try {
            return Files.size(dataFilePath);
        } catch (IOException e) {
            return -1;
        }
    }
}

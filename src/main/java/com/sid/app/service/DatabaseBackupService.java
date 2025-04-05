package com.sid.app.service;

import com.sid.app.config.DatabaseBackupProperties;
import com.sid.app.exception.DatabaseOperationException;
import com.sid.app.exception.SchemaNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Enhanced database backup service with database creation commands in backup files
 */
@Slf4j
@Service
public class DatabaseBackupService {

    private final DatabaseBackupProperties databaseBackupProperties;
    private Connection adminConnection;

    @Autowired
    public DatabaseBackupService(DatabaseBackupProperties databaseBackupProperties) {
        this.databaseBackupProperties = databaseBackupProperties;
        initializeAdminConnection();
    }

    private void initializeAdminConnection() {
        try {
            this.adminConnection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/postgres",
                    System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "postgres",
                    System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : "root"
            );
            log.info("Admin database connection initialized successfully");
        } catch (SQLException e) {
            log.error("Failed to initialize admin database connection", e);
            throw new DatabaseOperationException("Failed to initialize admin connection", e);
        }
    }

    public Mono<String> createBackupReactive(String type, String databaseName, String schemaName) {
        return Mono.fromCallable(() -> {
                    BackupType backupType = BackupType.valueOf(type.toUpperCase());
                    String dbName = databaseName != null ? databaseName : databaseBackupProperties.getDefaultDb();

                    boolean dbCreated = ensureDatabaseExists(dbName);
                    validateSchemaExists(dbName, schemaName);

                    String backupPath = createBackup(backupType, dbName, schemaName);

                    if (dbCreated && backupType == BackupType.SQL) {
                        addDatabaseCreationToBackup(dbName, backupPath);
                    }

                    return backupPath;
                })
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic());
    }

    /**
     * Ensures database exists, creates it if necessary
     *
     * @return true if database was created, false if it already existed
     */
    private boolean ensureDatabaseExists(String databaseName) throws SQLException {
        if (databaseExists(databaseName)) {
            log.debug("Database {} already exists", databaseName);
            return false;
        }

        log.info("Creating database: {}", databaseName);
        try (Statement stmt = adminConnection.createStatement()) {
            stmt.executeUpdate(String.format("CREATE DATABASE %s", databaseName));
            log.info("Database {} created successfully", databaseName);
            return true;
        } catch (SQLException e) {
            log.error("Failed to create database {}", databaseName, e);
            throw new DatabaseOperationException("Failed to create database: " + databaseName, e);
        }
    }

    /**
     * Adds database creation SQL to the beginning of the backup file
     */
    private void addDatabaseCreationToBackup(String databaseName, String backupPath) throws IOException {
        File backupFile = new File(backupPath);
        File tempFile = new File(backupPath + ".tmp");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
             BufferedReader reader = new BufferedReader(new FileReader(backupFile))) {

            // Write database creation commands
            writer.write(String.format("-- Database creation commands added by backup service%n"));
            writer.write(String.format("CREATE DATABASE %s;%n%n", databaseName));

            // Copy original content
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "%n");
            }
        }

        // Replace original file with modified one
        if (!backupFile.delete() || !tempFile.renameTo(backupFile)) {
            throw new IOException("Failed to update backup file with database creation commands");
        }

        log.info("Added database creation commands to backup file");
    }

    /**
     * Checks if database exists
     */
    private boolean databaseExists(String databaseName) throws SQLException {
        try (PreparedStatement stmt = adminConnection.prepareStatement(
                "SELECT 1 FROM pg_database WHERE datname = ?")) {
            stmt.setString(1, databaseName);
            return stmt.executeQuery().next();
        }
    }

    /**
     * Validates if schema exists in the specified database
     */
    private void validateSchemaExists(String databaseName, String schemaName)
            throws SQLException, SchemaNotFoundException {
        if (schemaName == null || schemaName.isEmpty()) {
            return;
        }

        try (Connection dbConn = getDatabaseConnection(databaseName);
             PreparedStatement stmt = dbConn.prepareStatement(
                     "SELECT schema_name FROM information_schema.schemata WHERE schema_name = ?")) {

            stmt.setString(1, schemaName);
            if (!stmt.executeQuery().next()) {
                String errorMsg = String.format("Schema '%s' does not exist in database '%s'",
                        schemaName, databaseName);
                log.error(errorMsg);
                throw new SchemaNotFoundException(errorMsg);
            }
            log.debug("Schema validation successful for schema: {}", schemaName);
        }
    }

    /**
     * Gets a connection to the specified database
     */
    private Connection getDatabaseConnection(String databaseName) throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/" + databaseName,
                System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "postgres",
                System.getenv("PGPASSWORD") != null ? System.getenv("PGPASSWORD") : "root"
        );
    }

    /**
     * Creates a database backup using pg_dump
     */
    private String createBackup(BackupType type, String databaseName, String schemaName)
            throws IOException, InterruptedException {

        log.info("Starting backup for database: {}, schema: {}", databaseName, schemaName);

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = String.format("%s_%s_%s_%s.%s",
                databaseBackupProperties.getPrefix(),
                databaseName,
                schemaName != null ? schemaName : "full",
                timestamp,
                type.getExtension());

        File backupDir = new File(databaseBackupProperties.getDirectory());
        if (!backupDir.exists() && !backupDir.mkdirs()) {
            throw new IOException("Failed to create backup directory: " + backupDir.getAbsolutePath());
        }

        String backupPath = backupDir.getAbsolutePath() + File.separator + fileName;
        log.info("Creating backup at: {}", backupPath);

        ProcessBuilder processBuilder = buildProcess(type, databaseName, schemaName, backupPath);
        Process process = processBuilder.start();

        logProcessOutput(process);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String errorMsg = "Backup failed with exit code " + exitCode;
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        log.info("Backup completed successfully");
        return backupPath;
    }

    /**
     * Builds the pg_dump command
     */
    private ProcessBuilder buildProcess(BackupType type, String databaseName, String schemaName, String backupPath) {
        List<String> command = new java.util.ArrayList<>();
        command.add("pg_dump");
        command.add("-U");
        command.add(System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "postgres");
        command.add("-h");
        command.add("localhost");
        command.add("-d");
        command.add(databaseName);

        if (schemaName != null && !schemaName.isEmpty()) {
            command.add("-n");
            command.add(schemaName);
        }

        command.add("-f");
        command.add(backupPath);
        command.add("--format=" + (type == BackupType.SQL ? "plain" : "custom"));

        log.debug("Executing command: {}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        if (System.getenv("PGPASSWORD") == null) {
            processBuilder.environment().put("PGPASSWORD", "root");
        }

        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    /**
     * Logs process output
     */
    private void logProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("pg_dump: {}", line);
            }
        }
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        try {
            if (adminConnection != null && !adminConnection.isClosed()) {
                adminConnection.close();
                log.info("Admin connection closed successfully");
            }
        } catch (SQLException e) {
            log.error("Error closing admin connection", e);
        }
    }

    /**
     * Backup format types
     */
    public enum BackupType {
        SQL("sql"),
        DUMP("dump");

        private final String extension;

        BackupType(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }

}
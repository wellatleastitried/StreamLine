package com.walit.streamline.Communicate;

/**
 * Error messages to be used throughout the program as needed.
 */
public enum StreamLineMessages {

    FatalStartError("[!] A fatal error has occured while starting StreamLine, please try reloading the app."),
    Farewell("[*] Thank you for using StreamLine!"),
    FatalPathError("[!] A fatal error has occured while retrieving the path of the database file, please try reloading the app."),
    DBCreationFailure("[!] A fatal error has occured while generating the schema of the database, please try reloading the app."),
    UnknownDBFatalError("[!] There has been an unknown fatal error while connecting to the database, please try reloading the app."),
    DBConnectionError("[!] A fatal error has occured while trying to connect to the existing database."),
    DBCloseError("[!] A fatal error has occured while closing the connection to the database."),
    GetDBConnectionFailure("[!] A fatal error has occured while establishing a connection to the database."),
    DatabaseFileCreationFailure("[!] A fatal error has occured while creating the database file, please try reloading the app."),
    SQLFileReadError("[!] A fatal error has occured while reading premade SQL queries from configuration files."),
    CorruptedFileHashError("[!] The integrity of the sql configuration files has been corrupted, if this problem persists you may need to rebuild this project."),
    HashingFileInputStreamError("[!] There has been an error reading the bytes from the configuration file, please try reloading the app."),
    HashMatchingError("[!] Error validating the integrity of the configuration files."),
    MissingConfigurationFiles("[!] Missing configuration files! You may need to rebuild the project."),
    InvalidPathForConfiguration("[!] The SQL configuration files are unable to be found! You may need to rebuild the project."),
    SQLQueryError("[!] Unable to execute query on the database, please try restarting the app."),
    RollbackError("[!] Unable to rollback changes to database after an error."),
    AutoCommitRestoreFailure("[!] Failed to restore auto-commit feature to connection, please restart the app."),
    DisableAutoCommitFailure("[!] Unable to disable auto-commit feature with connection, please restart the app."),
    CacheDirectoryCleanupFailure("[*] Error trying to clean cached songs."),
    AudioFileFormatError("[!] Error resolving file format, please try again."),
    AudioFetchFailure("[!] Error while fetching audio, please try again."),
    IOException("[!] IOException encountered during song playback, please try again.");

    private final String message;

    StreamLineMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

package com.walit.streamline.Communicate;

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
    CorruptedFileHashError("[!] The integrity of the sql configuration files has been corrupted, if this problem persists you may need to rebuild this project.");

    private final String message;

    StreamLineMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

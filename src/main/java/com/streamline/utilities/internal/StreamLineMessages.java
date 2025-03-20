package com.streamline.utilities.internal;

/**
 * Error messages to be used throughout the program as needed.
 * @author wellatleastitried
 */
public enum StreamLineMessages {

    FatalStartError("[!] A fatal error has occured while starting StreamLine, please try reloading the app."),
    InvidiousRepositoryHasNotBeenClonedWarning("[*] The Invidious repository has not been cloned, docker will not be able to produce an Invidious instance without it. Reload the application with the --setup flag to locally clone the repository."),
    IllegalStateExceptionInShutdown("[!] There was an exception while cleaning up the terminal interface:\n"),
    InvidiousBuildError("[!] An error occured while building the image for Invidious with Docker. Please try re-running the app with the --setup flag."),
    LoggerInitializationFailure("[!] There was an error while initializing the logger, please try reloading the app!"),
    IncorrectNumberOfResultsFromSongSearch("[*] There were either no results, or too many results for the song name you entered, try to be more specific with your search.\n[*] Example:\n[*] \tstreamline --play <songName> - <songAuthor>"),
    UnableToKillDockerContainer("[!] There was an error while trying to close the container!"),
    ErrorCloningRepository("[!] There was an error while trying to clone the Invidious repository, please try re-running the app with the --setup flag."),
    TooManyArgumentsProvided("[!] There were too many arguments provided. Only one option can be chosen at a time.\n\tUsage:\n\t\tstreamline [--OPTION] [ARGUMENT]\n"),
    UnableToPullSearchResultsFromYtDlp("[!] There was an error while using yt-dlp to gather results from the search query, please try again."),
    YtDlpDownloadFailed("[!] An error occured while downloading yt-dlp, please try again."),
    ErrorWritingToDockerCompose("[!] There was an error while parsing and writing docker-compose.yml, please re-run the app with the --setup flag"),
    ErrorRetrievingTokensForDockerCompose("[!] There was an error while retrieving the youtube validator tokens, please try again later."),
    ErrorReadingHostsFromResource("[!] Could not read hostnames from internal resource file, the installation may be corrupted!"),
    ShutdownTookTooLong("[!] The shutdown process of the app took too long, forcing shutdown..."),
    UnexpectedErrorInShutdown("[!] An unexpected error occured during shutdown, forcing shutdown..."),
    DockerNotRunningError("[!] Docker is not currently running on your machine, only offline functionality will be available."),
    GitNotInstalled("[!] Git is not installed on this device. Git must be installed before running setup!"),
    CommandRunFailure("[!] The system encountered an error while running the following command: "),
    DatabaseQueryCollectionError("[!] Encountered an error while retrieving queries from internal files. This is either due to the files being modified outside of the programs runtime, or a corrupted install."),
    PeriodicConnectionTestingError("[!] Encountered an error while attempting to establish a network connection in the background."),
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
    IOException("[!] IOException encountered during song playback, please try again."),
    UnableToCallAPIError("[!] Unable to connect to API at this time, please try again later."),
    JsonParsingException("[!] Unable to parse JSON response from API, please try again later."),
    RedrawError("[!] Error while redrawing screen, please restart the app."),
    DockerNotInstalledError("[!] Docker does not appear to be installed on this machine, only offline functionality will be available.");

    private final String message;

    StreamLineMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

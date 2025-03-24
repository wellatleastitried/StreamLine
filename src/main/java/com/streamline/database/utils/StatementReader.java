package com.streamline.database.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Parses the pre-made queries from the resource folder so that the queries can be loaded into in the backend.
 * @author wellatleastitried
 */
public class StatementReader {

    protected static boolean validatePath(String path) throws Exception {
        InputStream resource = StatementReader.class.getResourceAsStream(path);
        if (resource != null) {
            resource.close();
            return true;
        }
        return false;
    }

    /**
     * Reads the stored queries and returns them based on the filename provided.
     * @param pathToFile must start with a '/' and be a relative path from the /resources directory.
     * @return SQL query from the read file, or null if the filename does not exist.
     */
    public static String readQueryFromFile(String pathToFile) throws Exception {
        if (!validatePath(pathToFile)) {
            System.err.println("[!] The SQL configuration files are unable to be found! You may need to rebuild the project.");
            System.exit(1);
        }
        try (InputStream inputStream = StatementReader.class.getResourceAsStream(pathToFile);
                BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            /* Read the file and return the string of its contents (minus the first warning line) */
            StringBuilder query = new StringBuilder();
            String line;
            while ((line = bR.readLine()) != null) {
                query.append(line).append(System.lineSeparator());
            }
            return query.toString().trim();
        }
    }
}

package com.walit.streamline.Utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.walit.streamline.Communicate.StreamLineMessages;

public class StatementReader {

    protected static boolean validatePath(String path) {
        InputStream resource = StatementReader.class.getResourceAsStream(path);
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException iE) {
                System.err.println("Error closing stream while testing is resource is valid.");
            }
            return true;
        }
        return false;
    }

    /**
     * Reads the stored queries and returns them based on the filename provided.
     * @param pathToFile must start with a '/' and be a relative path from the /resources directory.
     * @return SQL query from the read file, or null if the filename does not exist.
     */
    public static String readQueryFromFile(String pathToFile) {
        if (!validatePath(pathToFile)) {
            System.err.println(StreamLineMessages.InvalidPathForConfiguration.getMessage());
            System.exit(1);
        }
        try (InputStream inputStream = StatementReader.class.getResourceAsStream(pathToFile);
             BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            // Read the file and return the string of its contents (minus the first warning line)
            StringBuilder query = new StringBuilder();
            String line;
            while ((line = bR.readLine()) != null) {
                query.append(line).append(System.lineSeparator());
            }
            return query.toString().trim();
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.SQLFileReadError.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(StreamLineMessages.MissingConfigurationFiles.getMessage());
            System.exit(1);
        }
        return null;
    }
}

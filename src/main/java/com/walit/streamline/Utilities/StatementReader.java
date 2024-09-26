package com.walit.streamline.Utilities;

import java.io.BufferedReader;
import java.io.File;
// import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
// import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Scanner;

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
        return false;;
    }
    // pathToFile Must start with a '/' and be a relative path from the /resources directory.
    public static String readQueryFromFile(String pathToFile) {
        if (!validatePath(pathToFile)) {
            System.err.println(StreamLineMessages.InvalidPathForConfiguration.getMessage());
            System.exit(1);
        }
        // Added this before I realized its completely unnecessary but this feature is here in case I have a need for it later lmfao
        /*
        // Validate the file we are pulling with the hash generated at build time
        final String generatedHash = generateHashFromFile(pathToFile);
        if (!generatedHashMatchesFileHash(generatedHash)) {
            System.err.println(StreamLineMessages.CorruptedFileHashError.getMessage());
            System.exit(1);
        }
        */
        try (InputStream inputStream = StatementReader.class.getResourceAsStream(pathToFile);
             BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            // Read the file and return the string of its contents (minus the first warning line)
            StringBuilder query = new StringBuilder();
            String line;
            boolean hasSkippedWarningLine = false;
            while ((line = bR.readLine()) != null) {
                if (!hasSkippedWarningLine) {
                    hasSkippedWarningLine = true;
                    continue;
                }
                query.append(line).append(System.lineSeparator());
            }
            System.err.println(query.toString());
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

    /*
    protected static boolean generatedHashMatchesFileHash(String generatedHash) {
        // Read hashes of configuration files from build file
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(String.format("integrityCheck/%s.txt", fileName));
            Scanner scanner = new Scanner(inputstream, StandardCharsets.UTF-8.name())) {
            return scanner.useDelimter("\\A").next();
        } catch (Exception e) {
            System.err.println(StreamLineMessages.HashMatchingError.getMessage());
            System.exit(1);
        }
        return buildHash.equals(generatedHash);
    }

    protected static String generateHashFromFile(String path) {
        try (FileInputStream fS = new FileInputStream(new File(path))) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fS.read(byteArray)) != -1) {
                digest.update(byteArray, 0, byteCount);
            }
            byte[] bytes = digest.digest();
            StringBuilder hexStringOfHash = new StringBuilder(new BigInteger(1, bytes).toString(16));
            while (hexStringOfHash.length() < 64) {
                hexStringOfHash.insert(0, '0');
            }
            return hexStringOfHash.toString();
        } catch (NoSuchAlgorithmException nA) {
            System.err.println("There is a typo in the name of the hashing algorithm being used.");
            System.exit(1);
        } catch (IOException iE) {
            System.err.println(StreamLineMessages.HashingFileInputStreamError.getMessage());
            System.exit(1);
        }
        return null;
    }
    */
}

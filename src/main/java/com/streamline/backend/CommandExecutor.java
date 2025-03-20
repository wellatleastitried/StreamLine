package com.streamline.backend;

import com.streamline.utilities.internal.StreamLineMessages;

import java.io.IOException;

import java.util.Arrays;

/**
 * Run shell commands to simplify repeated operations.
 * @author wellatleastitried
 */
public final class CommandExecutor {

    private CommandExecutor() {
        throw new AssertionError("Utility class should not be instantiated!");
    }

    public static Process runCommandExpectWait(String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            return process;
        } catch (IOException iE) {
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + command);
            return null;
        }
    }

    public static Process runCommandExpectWait(String[] splitCommand) {
        try {
            Process process = new ProcessBuilder(splitCommand).start();
            return process;
        } catch (IOException iE) {
            StringBuilder sB = new StringBuilder();
            Arrays.stream(splitCommand).forEach(str -> sB.append(str + " "));
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + sB.toString().trim());
            iE.printStackTrace();
            return null;
        }
    }
    
    public static boolean runCommand(String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (InterruptedException | IOException iE) {
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + command);
            return false;
        }
    }

    public static boolean runCommand(String[] splitCommand) {
        try {
            Process process = new ProcessBuilder(splitCommand).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (InterruptedException | IOException iE) {
            StringBuilder sB = new StringBuilder();
            Arrays.stream(splitCommand).forEach(str -> sB.append(str));
            System.out.println(StreamLineMessages.CommandRunFailure.getMessage() + sB.toString());
            return false;
        }
    }
}

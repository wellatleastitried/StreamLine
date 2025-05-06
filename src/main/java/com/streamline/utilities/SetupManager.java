package com.streamline.utilities;

import com.streamline.backend.DockerManager;
import com.streamline.backend.handle.YoutubeHandle;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.OS;
import com.streamline.utilities.internal.StreamLineConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.tinylog.Logger;

public class SetupManager {

    private static OS os = ConfigManager.getOSOfUser();

    private SetupManager() {}
    
    public static void installStreamLine() {
        boolean success = true;
        Logger.debug("[*] Path to jar: " + Paths.get(SetupManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
        System.out.println("[*] Installing StreamLine to your system's PATH for global access...");
        os = ConfigManager.getOSOfUser();
        if (os == OS.WINDOWS) {
            if (isRunningAsAdministrator()) {
                if (installForWindows()) {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                System.out.println("[!] You need to run this command with elevated privileges (e.g., `Administrator`) to install StreamLine globally.");
            }
        } else if (os == OS.MAC) {
            if (isRunningAsRoot()) {
                if (installForMac()) {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                System.out.println("[!] You need to run this command as root (e.g., `sudo`) to install StreamLine globally.");
            }
        } else {
            if (isRunningAsRoot()) {
                if (installForLinux()) {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                System.out.println("[!] You need to run this command as root (e.g., `sudo`) to install StreamLine globally.");
            }
        }

        if (success) {
            System.out.println("[*] StreamLine has been successfully installed.\nUse the command `streamline` to run the application.");
        } else {
            System.out.println("[!] An error occurred while installing StreamLine.");
        }
        System.exit(0);
    }

    public static void uninstallStreamLine() {
        boolean success = true;
        System.out.println("[*] Uninstalling StreamLine from your system's PATH...");
        os = ConfigManager.getOSOfUser();
        if (os == OS.WINDOWS) {
            if (isRunningAsAdministrator()) {
                if (uninstallForWindows()) {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                System.out.println("[!] You need to run this command with elevated privileges (e.g., `Administrator`) to uninstall StreamLine globally.");
            }
        } else if (os == OS.MAC) {
            if (isRunningAsRoot()) {
                if (uninstallForMac()) {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                System.out.println("[!] You need to run this command as root (e.g., `sudo`) to uninstall StreamLine globally.");
            }
        } else {
            if (isRunningAsRoot()) {
                if (uninstallForLinux()) {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                System.out.println("[!] You need to run this command as root (e.g., `sudo`) to uninstall StreamLine globally.");
            }
        }

        if (success) {
            System.out.println("[*] StreamLine has been successfully uninstalled.");
        } else {
            System.out.println("[!] An error occurred while uninstalling StreamLine.\nYou may need to manually remove the files from your system (Check the log file in the system's TEMP directory for the file paths to the installed files.");
        }
    }

    private static boolean uninstallForLinux() {
        new File(StreamLineConstants.LINUX_LAUNCHER_INSTALLATION_PATH + "streamline").delete();
        new File(StreamLineConstants.LINUX_JAR_INSTALLATION_PATH + "streamline.jar").delete();
        if (new File(StreamLineConstants.LINUX_LAUNCHER_INSTALLATION_PATH + "streamline").exists()) {
            Logger.warn("[!] The binary file at " + StreamLineConstants.LINUX_LAUNCHER_INSTALLATION_PATH + "streamline" + " could not be deleted.");
            return false;
        }
        if (new File(StreamLineConstants.LINUX_JAR_INSTALLATION_PATH + "streamline.jar").exists()) {
            Logger.warn("[!] The jar file at " + StreamLineConstants.LINUX_JAR_INSTALLATION_PATH + "streamline.jar" + " could not be deleted.");
            return false;
        }
        return true;
    }

    private static boolean uninstallForMac() {
        new File(StreamLineConstants.MAC_LAUNCHER_INSTALLATION_PATH + "streamline").delete();
        new File(StreamLineConstants.MAC_JAR_INSTALLATION_PATH + "streamline.jar").delete();
        if (new File(StreamLineConstants.MAC_LAUNCHER_INSTALLATION_PATH + "streamline").exists()) {
            Logger.warn("[!] The binary file at " + StreamLineConstants.MAC_LAUNCHER_INSTALLATION_PATH + "streamline" + " could not be deleted.");
            return false;
        }
        if (new File(StreamLineConstants.MAC_JAR_INSTALLATION_PATH + "streamline.jar").exists()) {
            Logger.warn("[!] The jar file at " + StreamLineConstants.MAC_JAR_INSTALLATION_PATH + "streamline.jar" + " could not be deleted.");
            return false;
        }
        return true;
    }

    private static boolean uninstallForWindows() {
        new File(StreamLineConstants.WINDOWS_LAUNCHER_INSTALLATION_PATH + "streamline.bat").delete();
        new File(StreamLineConstants.WINDOWS_JAR_INSTALLATION_PATH + "streamline.jar").delete();
        if (new File(StreamLineConstants.WINDOWS_LAUNCHER_INSTALLATION_PATH + "streamline.bat").exists()) {
            Logger.warn("[!] The binary file at " + StreamLineConstants.WINDOWS_LAUNCHER_INSTALLATION_PATH + "streamline.bat" + " could not be deleted.");
            return false;
        }
        if (new File(StreamLineConstants.WINDOWS_JAR_INSTALLATION_PATH + "streamline.jar").exists()) {
            Logger.warn("[!] The jar file at " + StreamLineConstants.WINDOWS_JAR_INSTALLATION_PATH + "streamline.jar" + " could not be deleted.");
            return false;
        }
        return true;
    }

    private static boolean installForLinux() {
        File jarFile = new File(SetupManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String opt = StreamLineConstants.LINUX_JAR_INSTALLATION_PATH;
        String bin = StreamLineConstants.LINUX_LAUNCHER_INSTALLATION_PATH;
        if (! new File(opt).exists()) {
            new File(opt).mkdirs();
        }
        if (! new File(bin).exists()) {
            new File(bin).mkdirs();
        }

        File binFile = new File(bin + "streamline");
        File optFile = new File(opt + "streamline.jar");

        if (binFile.exists()) {
            System.out.println("[!] A file already exists at " + binFile.getAbsolutePath() + ", please resolve this conflict and try again.");
            return false;
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(binFile))) {
                /* Create the bash script for /usr/local/bin/streamline */
                binFile.createNewFile();
                Logger.debug("[*] Binary file has been created at: " + binFile.getAbsolutePath());
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.setPosixFilePermissions(binFile.toPath(), permissions);
                Logger.debug("[*] Binary file has been set to executable.");
                String streamlineBinContents = "#!/bin/bash\njava -jar " + optFile.getAbsolutePath() + " $@";
                writer.write(streamlineBinContents);
                writer.flush();
                Logger.debug("[*] Binary file has been written to.");

                /* Copy the StreamLine jar file to /opt/StreamLine/streamline.jar */
                Files.copy(jarFile.toPath(), optFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                if (binFile.exists()) {
                    binFile.delete();
                }
                if (optFile.exists()) {
                    optFile.delete();
                }
                System.out.println("[!] An error occurred while writing to the file: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static boolean installForMac() {
        File jarFile = new File(SetupManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String appDir = StreamLineConstants.MAC_JAR_INSTALLATION_PATH;
        String binDir = StreamLineConstants.MAC_LAUNCHER_INSTALLATION_PATH;
        if (! new File(appDir).exists()) {
            new File(appDir).mkdirs();
        }
        if (! new File(binDir).exists()) {
            new File(binDir).mkdirs();
        }

        File binFile = new File(binDir + "streamline");
        File appFile= new File(appDir + "streamline.jar");

        if (binFile.exists()) {
            System.out.println("[!] A file already exists at " + binFile.getAbsolutePath() + ", please resolve this conflict and try again.");
            return false;
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(binFile))) {
                /* Create the bash script for /usr/local/bin/streamline */
                binFile.createNewFile();
                Logger.debug("[*] Binary file has been created at: " + binFile.getAbsolutePath());
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.setPosixFilePermissions(binFile.toPath(), permissions);
                Logger.debug("[*] Binary file has been set to executable.");
                String streamlineBinContents = "#!/bin/bash\njava -jar " + appFile.getAbsolutePath() + " $@";
                writer.write(streamlineBinContents);
                writer.flush();
                Logger.debug("[*] Binary file has been written to.");

                /* Copy the StreamLine jar file to /Applications/StreamLine/streamline.jar */
                Files.copy(jarFile.toPath(), appFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                if (binFile.exists()) {
                    binFile.delete();
                }
                if (appFile.exists()) {
                    appFile.delete();
                }
                System.out.println("[!] An error occurred while writing to the file: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static boolean installForWindows() {
        File jarFile = new File(SetupManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String appData = StreamLineConstants.WINDOWS_JAR_INSTALLATION_PATH;
        String appDataBin = StreamLineConstants.WINDOWS_LAUNCHER_INSTALLATION_PATH;
        if (! new File(appData).exists()) {
            new File(appData).mkdirs();
        }
        if (! new File(appDataBin).exists()) {
            new File(appDataBin).mkdirs();
        }

        File appDataBinFile = new File(appDataBin + "streamline.bat");
        File appDataFile = new File(appData + "streamline.jar");

        if (appDataBinFile.exists()) {
            System.out.println("[!] A file already exists at " + appDataBinFile.getAbsolutePath() + ", please resolve this conflict and try again.");
            return false;
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(appDataBinFile))) {
                /* Create the bash script for %LOCALAPPDATA%\StreamLine\bin\ */
                appDataBinFile.createNewFile();
                Logger.debug("[*] Binary file has been created at: " + appDataBinFile.getAbsolutePath());
                String streamlineBinContents = "@echo off\njava -jar " + appDataFile.getAbsolutePath() + " %*";
                writer.write(streamlineBinContents);
                writer.flush();
                Logger.debug("[*] Binary file has been written to.");

                /* Copy the StreamLine jar file to %LOCALAPPDATA%\StreamLine\streamline.jar */
                Files.copy(jarFile.toPath(), appDataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                if (appDataBinFile.exists()) {
                    appDataBinFile.delete();
                }
                if (appDataFile.exists()) {
                    appDataFile.delete();
                }
                System.out.println("[!] An error occurred while writing to the file: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static boolean isRunningAsRoot() {
        try {
            Process process = new ProcessBuilder("id", "-u").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String uid = reader.readLine();
            return "0".equals(uid);
        } catch (Exception e) {
            Logger.debug("[!] An error occurred while checking if the process is running as root: " + e.getMessage());
        }
        return false;
    }

    private static boolean isRunningAsAdministrator() {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "powershell",
                    "-Command",
                    "([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)"
                    );
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            return Boolean.parseBoolean(result);
        } catch (Exception e) {
            Logger.debug("[!] An error occurred while checking if the process is running as Administrator: " + e.getMessage());
        }
        return false;
    }

    public static void setupApi(CommandLine commandLine) {
        String choice = commandLine.getOptionValue("setup").toLowerCase();
        if (choice.contains("docker")) {
            handleDockerSetup();
        } else if (choice.contains("youtube")) {
            handleYoutubeSetup();
        } else {
            System.out.println("[!] Invalid argument passed for --setup");
        }
    }

    public static void handleDockerSetup() {
        DockerManager.cloneInvidiousRepo();
        boolean didWrite = DockerManager.writeDockerCompose();
        if (!didWrite) {
            System.out.println("[!] There was an error while parsing and writing docker-compose.yml, please re-run the app with the --setup flag");
        }
        if (DockerManager.buildInstance()) {
            System.out.println("\n[*] Invidious image built successfully!\n");
        } else {
            System.out.println("[!] An error occured while building the image for Invidious with Docker. Please try re-running the app with the --setup flag.");
        }
        System.exit(0);
    }

    public static void handleYoutubeSetup() {
        Config config = ConfigManager.getConfigurationForRuntime();
        if (YoutubeHandle.setupYoutubeInterop(config)) {
            System.out.println("[*] yt-dlp has been successfully downloaded.");
        } else {
            System.out.println("[!] An error was encountered while setting up yt-dlp.");
        }
        System.exit(0);
    }

    public static void cleanYoutubeInstall(Config config) {
        YoutubeHandle.clean(config);
    }

    public static void cleanDockerInstall(Config config) {
        DockerManager.clean(config);
    }
}

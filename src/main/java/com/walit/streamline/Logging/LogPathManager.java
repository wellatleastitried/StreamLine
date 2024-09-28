package com.walit.streamline.Logging;

import java.io.File;

public final class LogPathManager {
    public static String getLogFilePath() {
        String logDir;
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            logDir = System.getenv("TEMP") + File.separator +  "\\StreamLine";
        } else {
            logDir = "/tmp/StreamLine";
        }
        File dir = new File(logDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return logDir + File.separator + "streamline.log";
    }
}

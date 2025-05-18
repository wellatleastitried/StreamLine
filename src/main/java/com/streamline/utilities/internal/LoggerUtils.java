package com.streamline.utilities.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.tinylog.Logger;

public class LoggerUtils {

    public static void logErrorMessage(Exception e) {
        Writer buffer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(buffer);
        e.printStackTrace(printWriter);
        Logger.error("An error occured: {}", buffer.toString());
    }
}

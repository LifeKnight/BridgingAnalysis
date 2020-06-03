package com.lifeknight.bridginganalysis.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {
    private File logFile;
    private final File logFolder;
    private ArrayList<File> logFiles;
    private boolean doLog = true;
    private final boolean doLogTime;
    private String currentLog = "";
    private final ArrayList<String> logs = new ArrayList<>();

    public Logger(String name, File folder) {
        logFolder = folder;
        logFile = new File(logFolder + "/" + Misc.getCurrentDate().replace("/", ".") + ".txt");

        boolean logCreateFolder = logFolder.mkdirs();

        for (File file: Objects.requireNonNull(logFolder.listFiles())) {
            logs.add(logToString(file));
        }

        try {
            if (logFile.createNewFile()) {
                if (logCreateFolder) {
                    log("New folder & file created.");
                } else {
                    log("New file created.");
                }

            } else {
                getPreviousLog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.doLogTime = true;
        log("New logger created.");

        try {
            logFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(logFolder.listFiles())));
        } catch (Exception e) {
            logFiles = new ArrayList<>(Collections.singletonList(logFile));
            e.printStackTrace();
        }

    }

    public void getPreviousLog() {
        currentLog = logToString(logFile);
    }

    public String logToString(File log) {
        try {
            Scanner reader = new Scanner(log);

            StringBuilder logContent = new StringBuilder();

            while (reader.hasNextLine()) {
                logContent.append(reader.nextLine()).append(System.getProperty("line.separator"));
            }

            reader.close();

            return logContent.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void log(String input) {
        if (doLog) {
            if (doLogTime) {
                String currentTimeString = new SimpleDateFormat("hh:mm:ss a").format(new Date());
                currentLog += "[" + currentTimeString + "] " + input + System.getProperty("line.separator");
            }
            writeLogToFile();
        }
    }

    public void plainLog(String input) {
        if (doLog) {
            currentLog += input + System.getProperty("line.separator");
            writeLogToFile();
        }
    }

    public void writeLogToFile() {
        try {
            logFile = new File(logFolder + "/" + Misc.getCurrentDate().replace("/", ".") + ".txt");

            if (this.logFile.createNewFile()) {
                logFiles.add(logFile);
                logs.add(currentLog);
                currentLog = "";
                log("New file created.");
            }

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));

            writer.write(currentLog);

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLog() {
        return currentLog;
    }

    public ArrayList<String> getLogs() {
        ArrayList<String> result = logs;
        result.add(currentLog);
        return result;
    }

    public File getLogFile() {
        return logFile;
    }

    public ArrayList<File> getLogFiles() {
        return logFiles;
    }

    public void toggleLog() {
        if (doLog) {
            log("Pausing logs.");
            doLog = false;
        } else {
            doLog = true;
            log("Resuming logs.");
        }
    }

    public boolean isRunning() {
        return doLog;
    }
}

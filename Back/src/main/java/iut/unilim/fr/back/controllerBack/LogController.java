package iut.unilim.fr.back.controllerBack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogController {
    private static final String openSymbol = "[";
    private static final String closeSymbol = "] ";
    private static final String format = "dd:MM:yyyy HH:mm:ss:SS";
    private static String logsPath = "logs/"; // Final pour la securite, pour faire passer les tests, il faut enlever final
    // TODO : Gestion journalière de logs avec un fichier par jour
    public static void writeInPdfLog(String message) {
        String fileName = logsPath + ".pdf_log.txt";
        Path path = Paths.get(fileName);

        String logMessage = writeInLog(message, path);

        System.out.println(logMessage);
    }

    public static void writeInCsvLogs(String message) {
        String fileName = logsPath + ".csv_log.txt";
        Path path = Paths.get(fileName);

        String logMessage = writeInLog(message, path);
        System.out.println(logMessage);
    }

    public static void debugCsvLogs(Integer debugID, String debugName, String message) {
        String fileName = logsPath + ".csv_log.txt";
        Path path = Paths.get(fileName);

        String logMessage = debug(debugID, debugName, message, path);
        System.out.println(logMessage);
    }

    public static void writeInMailLogs(String message) {
        String fileName = logsPath + ".mail_log.txt";
        Path path = Paths.get(fileName);

        String logMessage = writeInLog(message, path);
        System.out.println(logMessage);
    }

    private static String writeInLog(String message, Path path) {
        SimpleDateFormat logDate = new SimpleDateFormat(format);
        String logMessage = openSymbol + logDate.format(new Date()) + closeSymbol + message + "\n";

        try {
            if (!Files.exists(path)) {
                logMessage = openSymbol + logDate.format(new Date()) + closeSymbol + "Create log file\n" + logMessage;
            }
            Files.writeString(
                    path,
                    logMessage,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        }
        catch (IOException e) {
            logMessage = openSymbol + logDate.format(new Date()) + closeSymbol + e.getMessage() + "\n";
        }
        return logMessage + "\n";
    }
    private static String debug(Integer debugId, String debugName, String message, Path path) {
        String logMessage = "DEBUG"+debugId+"!"+debugName.toUpperCase() + " " + message + "\n";

        try {
            if (!Files.exists(path)) {
                logMessage = "DEBUG"+debugId+"!"+debugName.toUpperCase()+ " " +"Create log file\n" + logMessage;
            }
            Files.writeString(
                    path,
                    logMessage,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        }
        catch (IOException e) {
            logMessage = "DEBUG"+debugId+"!"+debugName.toUpperCase() + " " + e.getMessage() + "\n";
        }
        return logMessage + "\n";
    }
}

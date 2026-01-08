package iut.unilim.fr.back.controllerBack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogController {
    public static void writeInPdfLog(String message) {
        String fileName = ".pdf_log.txt";
        Path path = Paths.get(fileName);
        
        String format = "dd:MM:yyyy HH:mm:ss:SS";
        SimpleDateFormat logDate = new SimpleDateFormat(format);
        String logMessage = "[" + logDate.format(new Date()) + "] " + message + "\n";

        try {
            if (!Files.exists(path)) {
                logMessage = "[" + logDate.format(new Date()) + "] Create log file\n" + logMessage;
            }
            Files.writeString(
                    path,
                    logMessage,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        }
        catch (IOException e) {
            logMessage = "[" + logDate.format(new Date()) + "] " + e.getMessage() + "\n";
        }

        System.out.println(logMessage);

    }
}

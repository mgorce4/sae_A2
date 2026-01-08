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
        String openSymbol = "[";
        String closeSymbol = "] ";
        Path path = Paths.get(fileName);
        
        String format = "dd:MM:yyyy HH:mm:ss:SS";
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

        System.out.println(logMessage);

    }
}

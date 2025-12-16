package iut.unilim.fr.back.controllerBack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogController {
    public static void writeInLog(String message) {
        // TODO Fichier de log
        String format = "dd:MM:yyyy HH:mm:ss:SS";
        SimpleDateFormat logDate = new SimpleDateFormat(format);

        System.out.println("[" + logDate.format(new Date()) + "] " + message);

    }
}

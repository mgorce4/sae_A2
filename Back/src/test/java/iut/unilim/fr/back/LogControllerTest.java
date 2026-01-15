package iut.unilim.fr.back;

import iut.unilim.fr.back.controllerBack.LogController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LogControllerTest {

    @TempDir
    Path tempDir;

    @Test
    void testWriteInPdfLog() throws Exception {
        Field pathField = LogController.class.getDeclaredField("logsPath");
        pathField.setAccessible(true);
        String tempPathString = tempDir.toString() + File.separator; // modify temporaly the log file. Not flood.
        pathField.set(null, tempPathString);

        String message = "LOREM IPSUM";
        LogController.writeInPdfLog(message);

        Path logFile = tempDir.resolve(".pdf_log.txt");
        assertTrue(Files.exists(logFile), "PDF file should be create");

        String content = Files.readString(logFile);
        assertTrue(content.contains(message), "PDF file should contain the log message");
        assertTrue(content.contains("Create log file"), "PDF file should contain his own creation's message");
    }

    @Test
    void testWriteInCsvLogs() throws Exception {
        Field pathField = LogController.class.getDeclaredField("logsPath");
        pathField.setAccessible(true);
        String tempPathString = tempDir.toString() + File.separator;
        pathField.set(null, tempPathString);

        String message = "LOREM IPSUM";
        LogController.writeInCsvLogs(message);

        Path logFile = tempDir.resolve(".csv_log.txt");
        assertTrue(Files.exists(logFile), "CSV log file should be create");

        String content = Files.readString(logFile);
        assertTrue(content.contains(message));
    }
}
package org.example;
import org.junit.jupiter.api.BeforeEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.*;
import static org.junit.jupiter.api.Assertions.*;

class FlexibleFormatterTest {
    FlexibleFormatter flexibleFormatter;
    Logger logger;

    private String shouldLog(Logger logger, String message, Level level, Formatter formatter) {
        if(formatter == null) {
            formatter = new SimpleFormatter();
        }
        ByteArrayOutputStream loggerContent = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(loggerContent);
        Formatter flexibleFormatter = new FlexibleFormatter();
        StreamHandler streamHandler = new StreamHandler(printStream, formatter);
        logger.addHandler(streamHandler);
        logger.log(level, message);
        streamHandler.flush();
        return loggerContent.toString();
    }
    @BeforeEach
    void setUp() {
        flexibleFormatter = new FlexibleFormatter();
        logger = Logger.getLogger("test");
    }


    @org.junit.jupiter.api.Test
    void testFormat() {
        flexibleFormatter.format(logger);
        String message = "test";
        String loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);
        assertEquals(message + '\n', loggedMessage);
    }

    @org.junit.jupiter.api.Test
    void testUnFormat() {
        flexibleFormatter.format(logger);
        flexibleFormatter.unFormat(logger);
        String message = "test";
        String loggedMessage = shouldLog(logger, message, Level.INFO, null);
        assertNotEquals(message, loggedMessage);
        assertTrue(loggedMessage.contains(message));
    }

    @org.junit.jupiter.api.Test
    void testSetColor() {
        // ensure the logged starts with the correct color
        flexibleFormatter.setColor(FlexibleFormatter.FlexibleColor.BLUE);
        flexibleFormatter.format(logger);
        String message = "test";
        String loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);
        assertTrue(loggedMessage.startsWith("\u001B[34m"));
        // ensure the logged String ends with the correct color
        assertTrue(loggedMessage.endsWith("\u001B[0m\n"));
    }

    @org.junit.jupiter.api.Test
    void testEnableDate() {
        // ensure the logged message contains the date
        String datePattern = "MM/dd/yyyy HH:mm:ss";
        String patternStr = "\\[\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}].*";
        flexibleFormatter
                .enableDate(true)
                .setDatePattern(datePattern)
                .format(logger);

        String message = "test";
        String loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);

        Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL); // Utiliser le mode DOTALL
        Matcher matcher = pattern.matcher(loggedMessage);

        assertTrue(matcher.matches());

        flexibleFormatter.enableDate(false);
        loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);
        assertFalse(loggedMessage.matches(patternStr));
    }

    @org.junit.jupiter.api.Test
    void testEnableLevel() {
        // ensure the logged message contains the level
        flexibleFormatter.enableLevel(true).format(logger);
        String message = "test";
        String loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);
        assertTrue(loggedMessage.startsWith("[INFO]"));
        flexibleFormatter.enableLevel(false);
        loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);
        assertFalse(loggedMessage.startsWith("[INFO]"));
    }

    @org.junit.jupiter.api.Test
    void testEnableName() {
        // ensure the logged message contains the name
        flexibleFormatter.enableName(true).format(logger);
        String message = "test";
        String loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);
        assertTrue(loggedMessage.startsWith("[" + logger.getName() + "]"));
        flexibleFormatter.enableName(false);
        loggedMessage = shouldLog(logger, message, Level.INFO, flexibleFormatter);
        assertFalse(loggedMessage.startsWith("[" + logger.getName() + "]"));
    }
}
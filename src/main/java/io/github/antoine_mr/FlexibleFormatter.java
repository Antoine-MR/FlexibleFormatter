package io.github.antoine_mr;

import java.util.*;
import java.util.logging.*;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;


/**
 * @author Antoine Maïstre-Rice
 */
public class FlexibleFormatter extends Formatter {
    private final List<Logger> localFormatted = new ArrayList<>();
    public enum FlexibleColor {BLUE, GREEN, PURPLE, RED, WHITE, YELLOW, DEFAULT}
    private String datePattern = "MM/dd/yyyy HH:mm:ss";
    private FlexibleColor color = FlexibleColor.DEFAULT;
    private boolean levelBool = false;
    private boolean nameBool  = false;
    private boolean dateBool = false;
    private String getName(LogRecord l){
        if(nameBool)
            return "[" + l.getLoggerName() + "]";
        return "";
    }
    private String getLevel(LogRecord l){
        if(levelBool)
            return "[" + l.getLevel().toString() + "]";
        return "";
    }
    private String getDate(){
        if(dateBool)
            return "[" +  new SimpleDateFormat(datePattern).format(Calendar.getInstance().getTime()) + "]";
        return "";
    }

    /**
     * Configures the given java.util.logging.Logger to use a ConsoleHandler with this FlexibleFormatter.
     * If the Logger has already been formatted by this FlexibleFormatter, no changes are made.
     *
     * @param logger the Logger to be formatted. Must not be null.
     * If the Logger has already been formatted by this FlexibleFormatter, it will not be formatted again.
     * @return This FlexibleFormatter instance, allowing for method chaining.
     */
    public FlexibleFormatter format(Logger logger) {
        if (this.localFormatted.contains(logger))
            return this;
        localFormatted.add(logger);
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(this);
        logger.addHandler(handler);
        return this;
    }

    /**
     * Unset the current java.util.logging.Logger formatter
     * @param logger the Logger to be unFormatted. Must not be null.
     * If the Logger has not already been formatted by this FlexibleFormatter, nothing will append.
     * @return This FlexibleFormatter instance, allowing for method chaining.
     */
    public FlexibleFormatter unFormat(Logger logger) {
        if (!this.localFormatted.contains(logger)) {
            return this;
        }
        this.localFormatted.remove(logger);
        logger.setUseParentHandlers(true);

        for (Handler handler : logger.getHandlers()) {
            if (handler.getFormatter() instanceof FlexibleFormatter) {
                logger.removeHandler(handler);
            }
        }
        return this;
    }


    @Override
    @Deprecated
    public String format(LogRecord content) {

        String date    = this.getDate();
        String name    = this.getName(content);
        String level   = this.getLevel(content);
        String message = content.getMessage();

        List<String> values = new ArrayList<>(){{
            add(date);
            add(name);
            add(level);
        }};

        StringBuilder result = new StringBuilder();
        result.append(FlexibleFormatter.getANSIFromColor(this.color));
        for (String i : values){
            boolean activated = !i.isEmpty();
            if(activated){
                result.append(i).append(" ");
            }
        }
        result.append(message);

        if(this.color != FlexibleColor.DEFAULT)
            result.append("\u001B[0m");

        return result.append("\n").toString();
    }

    /**
     * Set the color of the logger to the given color in parameters
     * @param color The color that will be set.
     * @return This FlexibleFormatter instance, allowing for method chaining.
     */
    public FlexibleFormatter setColor(FlexibleColor color){
        this.color = color;
        return this;
    }

    /**
     * Enables or disables the date printing of the logger
     * @param enable True to enable, false to disable
     * @return This FlexibleFormatter instance, allowing for method chaining.
     */
    public FlexibleFormatter enableDate(boolean enable){
        this.dateBool = enable;
        return this;
    }


    /**
     * Enables or disables the level printing of the logger
     * @param enable True to enable, false to disable
     * @return This FlexibleFormatter instance, allowing for method chaining.
     */
    public FlexibleFormatter enableLevel(boolean enable){
        this.levelBool = enable;
        return this;
    }

    /**
     * Enables or disables the name printing of the logger
     * @param enable True to enable, false to disable
     * @return This FlexibleFormatter instance, allowing for method chaining.
     */
    public FlexibleFormatter enableName(boolean enable){
        this.nameBool = enable;
        return this;
    }

    /**
     * Returns the current color of the logger
     * @return The current color of the logger
     */
    public String getDatePattern() {
        return datePattern;
    }

    /**
     * Sets the date pattern of the logger
     * @param datePattern The date pattern that will be set.
     * @return This FlexibleFormatter instance, allowing for method chaining.
     */
    public FlexibleFormatter setDatePattern(String datePattern) {
        this.datePattern = datePattern;
        return this;
    }

    private static String getANSIFromColor(FlexibleColor c) {
        return switch (c) {
            case BLUE -> "\u001B[34m";
            case GREEN -> "\u001B[32m";
            case PURPLE -> "\u001B[35m";
            case RED -> "\u001B[31m";
            case WHITE -> "\u001B[37m";
            case YELLOW -> "\u001B[33m";
            case DEFAULT -> "";
        };
    }
}

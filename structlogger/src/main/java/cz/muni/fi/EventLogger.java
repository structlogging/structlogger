package cz.muni.fi;

import org.slf4j.helpers.MessageFormatter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Slf4j logger wrapper for serializing objects.
 * Used internally by structlogger, you should not need to use this
 */
public class EventLogger<T extends VariableContext> {

    public static final AtomicLong SEQ_NUMBER = new AtomicLong(); //must be static, generated events use it to generate sequence id

    public LoggingCallback callback;

    public EventLogger(final LoggingCallback callback) {
        this.callback = callback;
    }

    /**
     * log event on info level
     */
    public void info(final LoggingEvent e) {
        callback.info(e);
    }

    /**
     * log event on debug level
     */
    public void debug(final LoggingEvent e) {
        callback.debug(e);
    }

    /**
     * log event on error level
     */
    public void error(final LoggingEvent e) {
        callback.error(e);
    }

    /**
     * log event on warn level
     */
    public void warn(final LoggingEvent e) {
        callback.warn(e);
    }

    /**
     * log event on trace level
     */
    public void trace(final LoggingEvent e) {
        callback.trace(e);
    }

    // this are used just as placeholders, they should not be called at runtime,
    // calls to these methods are replaced by annotation processor to calls to correct method which accepts LoggingEvent
    // these methods are just used to give programmer a nice way to work with fluent logging API instead of creating logging events manually

    public T debug(String message) {
        return null;
    }

    public T info(String message) {
        return null;
    }

    public T error(String message) {
        return null;
    }

    public T warn(String message) {
        return null;
    }

    public T trace(String message) {
        return null;
    }
    /////////////////////////////////////////////////////////////

    /**
     * based on String pattern, which contains placeholder <code>{}</code>, inserts params into
     * the pattern and returns resulting String
     * @param pattern
     * @param params
     * @return String with params inserted into pattern
     */
    static String format(final String pattern, Object... params) {
        return MessageFormatter.arrayFormat(pattern, params).getMessage();
    }
}

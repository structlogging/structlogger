package cz.muni.fi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Slf4j logger wrapper for serializing objects.
 * Used internally by structlogger, you should not need to use this
 */
public class EventLogger<T extends VariableContext> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final AtomicLong SEQ_NUMBER = new AtomicLong();
    private final Logger logger;

    public EventLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * log event on info level
     */
    public void info(final LoggingEvent o) {
        try {
            this.logger.info(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log event on debug level
     */
    public void debug(final LoggingEvent o) {
        try {
            this.logger.debug(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log event on error level
     */
    public void error(final LoggingEvent o) {
        try {
            this.logger.error(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log event on warn level
     */
    public void warn(final LoggingEvent o) {
        try {
            this.logger.warn(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T debug(String message) {
        return null;
    };

    public T info(String message) {
        return null;
    };

    public T error(String message) {
        return null;
    };

    public T warn(String message) {
        return null;
    };

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

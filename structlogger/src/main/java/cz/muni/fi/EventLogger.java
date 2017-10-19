package cz.muni.fi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Slf4j logger wrapper for serializing objects.
 * Used internally by structlogger, you should not need to use this
 */
public class EventLogger {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final AtomicLong seqNumber = new AtomicLong();
    private final Logger logger;

    public EventLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * log event on info level
     */
    public void info(final Object o) {
        try {
            this.logger.info(MAPPER.writeValueAsString(createWrappedLogEvent(o, "INFO")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log event on debug level
     */
    public void debug(final Object o) {
        try {
            this.logger.debug(MAPPER.writeValueAsString(createWrappedLogEvent(o, "DEBUG")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log event on error level
     */
    public void error(final Object o) {
        try {
            this.logger.error(MAPPER.writeValueAsString(createWrappedLogEvent(o, "ERROR")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * log event on warn level
     */
    public void warn(final Object o) {
        try {
            this.logger.warn(MAPPER.writeValueAsString(createWrappedLogEvent(o, "WARN")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WrappedLogEvent createWrappedLogEvent(final Object o, final String level) {
        return new WrappedLogEvent(o, seqNumber.incrementAndGet(), level, o.getClass().getSimpleName());
    }
}

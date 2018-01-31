package cz.muni.fi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

/**
 * Logging callback which serializes events as string and pass them to SLF4j inside log message
 */
public class Slf4jLoggingCallback implements LoggingCallback {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String AUDIT = "AUDIT";
    private final Logger logger;

    public Slf4jLoggingCallback(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final LoggingEvent e) {
        try {
            logger.info(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void warn(final LoggingEvent e) {
        try {
            logger.warn(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void debug(final LoggingEvent e) {
        try {
            logger.debug(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void error(final LoggingEvent e) {
        try {
            logger.error(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void trace(final LoggingEvent e) {
        try {
            logger.trace(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    /**
     * This implementation uses INFO level of slf4j and marks these logged messages with AUDIT marker
     */
    @Override
    public void audit(final LoggingEvent e) {
        try {
            logger.info(MarkerFactory.getMarker(AUDIT), MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }
}

package cz.muni.fi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

/**
 * Logging callback which serializes events as string and pass them to SLF4j
 */
public class Slf4jLoggingCallback implements LoggingCallback {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Logger logger;

    public Slf4jLoggingCallback(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final LoggingEvent e) {
        try {
            this.logger.info(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void warn(final LoggingEvent e) {
        try {
            this.logger.warn(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void debug(final LoggingEvent e) {
        try {
            this.logger.debug(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void error(final LoggingEvent e) {
        try {
            this.logger.error(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }
}

package cz.muni.fi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

/**
 * Slf4j logger wrapper for serializing objects
 */
public class EventLogger {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Logger logger;

    public EventLogger() {
        logger = null;
    }

    public EventLogger(final Logger logger) {
        this.logger = logger;
    }

    public void info(final Object o) {
        try {
            this.logger.info(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void debug(final Object o) {
        try {
            this.logger.debug(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error(final Object o) {
        try {
            this.logger.error(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void warn(final Object o) {
        try {
            this.logger.warn(MAPPER.writeValueAsString(o));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

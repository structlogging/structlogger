package cz.muni.fi;

/**
 * Logging callback API
 */
public interface LoggingCallback {

    /**
     * corresponds to INFO level of logging
     */
    void info(LoggingEvent e);

    /**
     * corresponds to WARN level of logging
     */
    void warn(LoggingEvent e);

    /**
     * corresponds to DEBUG level of logging
     */
    void debug(LoggingEvent e);

    /**
     * corresponds to ERROR level of logging
     */
    void error(LoggingEvent e);

    /**
     * corresponds to TRACE level of logging
     */
    void trace(LoggingEvent e);

    /**
     * logging of audit related events
     */
    void audit(LoggingEvent e);
}

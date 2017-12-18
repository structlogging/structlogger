package cz.muni.fi;

/**
 * Logging callback API
 */
public interface LoggingCallback {
    void info(LoggingEvent e);

    void warn(LoggingEvent e);

    void debug(LoggingEvent e);

    void error(LoggingEvent e);

    void trace(LoggingEvent e);
}

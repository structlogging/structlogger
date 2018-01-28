package cz.muni.fi.processor;

/**
 * supported log levels and their corresponding names and method names
 */
public enum LogLevel {
    INFO("INFO", "info"),
    DEBUG("DEBUG", "debug"),
    ERROR("ERROR", "error"),
    TRACE("TRACE", "trace"),
    AUDIT("AUDIT", "audit"),
    WARN("WARN", "warn");

    private final String levelName;
    private final String levelMethodName;

    LogLevel(final String levelName, final String levelMethodName) {
        this.levelName = levelName;
        this.levelMethodName = levelMethodName;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelMethodName() {
        return levelMethodName;
    }
}

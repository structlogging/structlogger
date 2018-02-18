package cz.muni.fi.processor;

/**
 * supported log levels and their corresponding names and method names
 */
public enum LogLevel {
    INFO("INFO", "info", "infoEvent"),
    DEBUG("DEBUG", "debug", "debugEvent"),
    ERROR("ERROR", "error", "errorEvent"),
    TRACE("TRACE", "trace", "traceEvent"),
    AUDIT("AUDIT", "audit", "auditEvent"),
    WARN("WARN", "warn", "warnEvent");

    private final String levelName;
    private final String levelMethodName;
    private final String logEventMethodName;

    LogLevel(final String levelName, final String levelMethodName, final String logEventMethodName) {
        this.levelName = levelName;
        this.levelMethodName = levelMethodName;
        this.logEventMethodName = logEventMethodName;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelMethodName() {
        return levelMethodName;
    }

    public String getLogEventMethodName() {
        return logEventMethodName;
    }
}

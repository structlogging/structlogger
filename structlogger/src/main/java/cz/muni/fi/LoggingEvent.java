package cz.muni.fi;

/**
 * All generated logging events should extend this class
 * All changes made to this class should be reflected in LogInvocationScanner and POJOService or you risk incorrect behaviour of processor
 */
public class LoggingEvent {
    private final String message;
    private final String sourceFile;
    private final Long lineNumber;
    private final String type;
    private final Long sid;
    private final String logLevel;

    public LoggingEvent(final String message, final String sourceFile, final Long lineNumber, final String type, final Long sid, final String logLevel) {
        this.message = message;
        this.sourceFile = sourceFile;
        this.lineNumber = lineNumber;
        this.type = type;
        this.sid = sid;
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public Long getLineNumber() {
        return lineNumber;
    }

    public String getType() {
        return type;
    }

    public Long getSid() {
        return sid;
    }

    public String getLogLevel() {
        return logLevel;
    }
}

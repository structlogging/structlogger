package cz.muni.fi;

/**
 * This class encapsulates log events, contains also sequence id and log level and identifier of encapsulated event
 */
public class WrappedLogEvent {

    private Object event;
    private long sid;
    private String logLevel;
    private String eventIdentifier;

    public WrappedLogEvent(final Object event, final long sid, final String logLevel, final String eventIdentifier) {
        this.event = event;
        this.sid = sid;
        this.logLevel = logLevel;
        this.eventIdentifier = eventIdentifier;
    }

    public Object getEvent() {
        return event;
    }

    public long getSid() {
        return sid;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getEventIdentifier() {
        return eventIdentifier;
    }
}

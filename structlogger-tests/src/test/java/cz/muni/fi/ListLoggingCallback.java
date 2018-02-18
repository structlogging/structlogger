package cz.muni.fi;

import cz.muni.fi.LoggingCallback;
import cz.muni.fi.LoggingEvent;

import java.util.LinkedList;
import java.util.List;

public class ListLoggingCallback implements LoggingCallback {

    private final List<LoggingEvent> loggingEventList = new LinkedList<>();

    @Override
    public void info(final LoggingEvent e) {
        loggingEventList.add(e);
    }

    @Override
    public void warn(final LoggingEvent e) {
        loggingEventList.add(e);
    }

    @Override
    public void debug(final LoggingEvent e) {
        loggingEventList.add(e);
    }

    @Override
    public void error(final LoggingEvent e) {
        loggingEventList.add(e);
    }

    @Override
    public void trace(final LoggingEvent e) {
        loggingEventList.add(e);
    }

    @Override
    public void audit(final LoggingEvent e) {
        loggingEventList.add(e);
    }

    public List<LoggingEvent> getLoggingEventList() {
        return loggingEventList;
    }
}

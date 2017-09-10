package org.ngmon.structlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public interface StructLogger<T extends VariableContext> {

    Logger logger = LoggerFactory.getLogger(StructLogger.class);
    EventLogger eventLogger = new EventLogger(logger);

    T debug(String message);

    T info(String message);

    T error(String message);

    T warn(String message);

    static <T extends VariableContext> StructLogger<T> instance() {
        return null;
    }

    static String format(final String pattern, Object... params) {
        return MessageFormatter.arrayFormat(pattern, params).getMessage();
    }
}

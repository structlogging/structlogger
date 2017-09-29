package org.ngmon.structlog;

import org.slf4j.helpers.MessageFormatter;

/**
 * Dummy logger interface for creating logger fields
 * @param <T> class extending {@link VariableContext} to provide fluent API for logging
 *
 * Typical usage:
 * <code>
 * public class Example {
 *      @VarContext(context = DefaultContext.class)
 *      private static StructLogger<DefaultContext> defaultLog = StructLogger.instance();
 * }
 * </code>
 *
 * StructLogger field can then be used for structured logging
 */
public interface StructLogger<T extends VariableContext> {

    T debug(String message);

    T info(String message);

    T error(String message);

    T warn(String message);

    static <T extends VariableContext> StructLogger<T> instance() {
        return null;
    }

    /**
     * based on String pattern, which contains placeholder <code>{}</code>, inserts params into
     * the pattern and returns resulting String
     * @param pattern
     * @param params
     * @return String with params inserted into pattern
     */
    static String format(final String pattern, Object... params) {
        return MessageFormatter.arrayFormat(pattern, params).getMessage();
    }
}

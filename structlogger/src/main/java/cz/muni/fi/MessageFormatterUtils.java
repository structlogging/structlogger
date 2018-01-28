package cz.muni.fi;

import org.slf4j.helpers.MessageFormatter;

public final class MessageFormatterUtils {

    private MessageFormatterUtils() { }

    /**
     * based on String pattern, which contains placeholder <code>{}</code>, inserts params into
     * the pattern and returns resulting String
     * @param pattern
     * @param params
     * @return String with params inserted into pattern
     */
    public static String format(final String pattern, Object... params) {
        return MessageFormatter.arrayFormat(pattern, params).getMessage();
    }
}

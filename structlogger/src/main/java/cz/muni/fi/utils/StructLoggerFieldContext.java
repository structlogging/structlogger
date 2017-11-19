package cz.muni.fi.utils;

import javax.lang.model.type.TypeMirror;

/**
 * Holder for context provider type and logger name specified by {@link cz.muni.fi.annotation.LoggerContext} annotation on {@link cz.muni.fi.StructLogger} field
 */
public class StructLoggerFieldContext {

    private final TypeMirror contextProvider;
    private final String loggerName;

    public StructLoggerFieldContext(final TypeMirror contextProvider, final String loggerName) {
        this.contextProvider = contextProvider;
        this.loggerName = loggerName;
    }

    public TypeMirror getContextProvider() {
        return contextProvider;
    }

    public String getLoggerName() {
        return loggerName;
    }
}

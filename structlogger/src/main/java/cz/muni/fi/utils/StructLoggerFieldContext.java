package cz.muni.fi.utils;

import javax.lang.model.type.TypeMirror;

/**
 * Holder for context provider type specified by {@link cz.muni.fi.annotation.LoggerContext} annotation on {@link cz.muni.fi.StructLogger} field
 */
public class StructLoggerFieldContext {

    private final TypeMirror contextProvider;

    public StructLoggerFieldContext(final TypeMirror contextProvider) {
        this.contextProvider = contextProvider;
    }

    public TypeMirror getContextProvider() {
        return contextProvider;
    }
}

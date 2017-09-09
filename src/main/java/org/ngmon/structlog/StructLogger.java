package org.ngmon.structlog;

public interface StructLogger<T extends VariableContext> {

    T debug(String message);

    T info(String message);

    T error(String message);

    T warn(String message);

    static <T extends VariableContext> StructLogger<T> instance() {
        return null;
    }
}

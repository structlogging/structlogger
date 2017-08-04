package org.ngmon.structlog;

public interface StructLog<T extends VariableContext> {

    T debug(String message);

    T info(String message);

    T error(String message);

    static <T extends VariableContext> StructLog<T> instance() {
        return null;
    }
}

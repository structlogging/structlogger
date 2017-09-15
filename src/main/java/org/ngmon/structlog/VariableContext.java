package org.ngmon.structlog;

/**
 * Interface to be extended by concrete variable context interface
 * providing concrete logging variables
 */
public interface VariableContext {
    void log();
}

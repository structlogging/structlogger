package cz.muni.fi;

/**
 * Interface to be extended by concrete variable context interface
 * providing concrete logging variables
 */
public interface VariableContext {
    void log();

    void log(final String name);
}

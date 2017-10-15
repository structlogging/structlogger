package cz.muni.fi;

import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;

/**
 * Default {@link VariableContext} containing basic variables
 */
@VarContextProvider
public interface DefaultContext extends VariableContext {

    @Var
    DefaultContext varLong(long var);

    @Var
    DefaultContext varString(String var);

    @Var
    DefaultContext varInt(int var);

    @Var
    DefaultContext varDouble(double var);

    @Var
    DefaultContext varBoolean(boolean var);
}

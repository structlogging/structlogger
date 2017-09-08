package org.ngmon.structlog;

import org.ngmon.structlog.annotation.Var;
import org.ngmon.structlog.annotation.VarContextProvider;

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

package cz.muni.fi;

import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;

@VarContextProvider
public interface AuditContext extends VariableContext {

    @Var
    AuditContext id(long id);
}

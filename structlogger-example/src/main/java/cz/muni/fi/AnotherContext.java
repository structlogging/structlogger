package cz.muni.fi;

import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;

@VarContextProvider
public interface AnotherContext extends VariableContext {

    @Var
    AnotherContext context(String context);

}

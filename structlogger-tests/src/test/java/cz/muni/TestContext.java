package cz.muni;

import cz.muni.fi.VariableContext;
import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;

@VarContextProvider
public interface TestContext extends VariableContext {

    @Var
    TestContext varInt(int param);

    @Var
    TestContext varString(String param);
}

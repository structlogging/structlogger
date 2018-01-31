package cz.muni.fi;

import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;

@VarContextProvider
public interface DefaultContextWithoutParametrization extends VariableContext {

    @Var
    DefaultContextWithoutParametrization varLong(long var1);

    @Var
    DefaultContextWithoutParametrization varString(String var1);

    @Var
    DefaultContextWithoutParametrization varInt(int var1);

    @Var
    DefaultContextWithoutParametrization varDouble(double var1);

    @Var
    DefaultContextWithoutParametrization varBoolean(boolean var1);
}

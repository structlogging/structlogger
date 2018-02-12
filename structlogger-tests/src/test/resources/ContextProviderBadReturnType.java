import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

@VarContextProvider
public interface ContextProviderBadReturnType extends VariableContext {

    @Var
    void varLong(long var);

    @Var
    ContextProviderBadReturnType varString(String var);

    @Var
    ContextProviderBadReturnType varInt(int var);

    @Var
    ContextProviderBadReturnType varDouble(double var);

    @Var
    ContextProviderBadReturnType varBoolean(boolean var);
}
import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

@VarContextProvider
public interface ContextProviderNotExtending {

    @Var
    ContextProviderNotExtending varLong(long var);

    @Var
    ContextProviderNotExtending varString(String var);

    @Var
    ContextProviderNotExtending varInt(int var);

    @Var
    ContextProviderNotExtending varDouble(double var);

    @Var
    ContextProviderNotExtending varBoolean(boolean var);
}

import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

@VarContextProvider
public interface ContextProviderMultipleArgumentVar extends VariableContext {

    @Var
    ContextProviderMultipleArgumentVar varLong(long var, long var2);

    @Var
    ContextProviderMultipleArgumentVar varString(String var);

    @Var
    ContextProviderMultipleArgumentVar varInt(int var);

    @Var
    ContextProviderMultipleArgumentVar varDouble(double var);

    @Var
    ContextProviderMultipleArgumentVar varBoolean(boolean var);
}
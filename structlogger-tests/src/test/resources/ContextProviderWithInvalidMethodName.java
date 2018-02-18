import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

@VarContextProvider
public interface ContextProviderWithInvalidMethodName extends VariableContext {

    @Var
    ContextProviderWithInvalidMethodName varLong(long var);

    @Var
    ContextProviderWithInvalidMethodName warnEvent(String var);
}

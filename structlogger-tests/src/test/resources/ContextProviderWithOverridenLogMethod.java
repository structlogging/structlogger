import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

@VarContextProvider
public interface ContextProviderWithOverridenLogMethod extends VariableContext {

    @Var
    ContextProviderBadMethodNames log(long var);
}

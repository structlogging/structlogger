import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

@VarContextProvider
public interface ContextProviderNoVar extends VariableContext {

    ContextProviderNoVar varLong(long var);

    ContextProviderNoVar varString(String var);
}
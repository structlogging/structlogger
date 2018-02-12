import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

@VarContextProvider
public interface ContextProviderOneVarMissing extends VariableContext {

    @Var
    ContextProviderOneVarMissing varLong(long var);

    ContextProviderOneVarMissing varString(String var);
}

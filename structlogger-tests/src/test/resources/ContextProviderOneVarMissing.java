import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

@VarContextProvider
public interface ContextProviderOneVarMissing extends VariableContext {

    @Var
    ContextProviderOneVarMissing varLong(long var);

    ContextProviderOneVarMissing varString(String var);
}

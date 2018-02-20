import com.github.tantalor93.annotation.Var;
import com.github.tantalor93.annotation.VarContextProvider;
import com.github.tantalor93.VariableContext;

@VarContextProvider
public interface ContextProviderOneVarMissing extends VariableContext {

    @Var
    ContextProviderOneVarMissing varLong(long var);

    ContextProviderOneVarMissing varString(String var);
}

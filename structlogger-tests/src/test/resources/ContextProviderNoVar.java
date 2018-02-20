import com.github.tantalor93.annotation.Var;
import com.github.tantalor93.annotation.VarContextProvider;
import com.github.tantalor93.VariableContext;

@VarContextProvider
public interface ContextProviderNoVar extends VariableContext {

    ContextProviderNoVar varLong(long var);

    ContextProviderNoVar varString(String var);
}
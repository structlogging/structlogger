import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

@VarContextProvider
public interface ContextProviderNoVar extends VariableContext {

    ContextProviderNoVar varLong(long var);

    ContextProviderNoVar varString(String var);
}
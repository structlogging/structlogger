import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

@VarContextProvider
public interface ContextProviderWithInvalidMethodName extends VariableContext {

    @Var
    ContextProviderWithInvalidMethodName varLong(long var);

    @Var
    ContextProviderWithInvalidMethodName warnEvent(String var);
}

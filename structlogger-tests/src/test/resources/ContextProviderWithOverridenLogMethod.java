import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

@VarContextProvider
public interface ContextProviderWithOverridenLogMethod extends VariableContext {

    @Var
    ContextProviderBadMethodNames log(long var);
}

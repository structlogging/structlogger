import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

@VarContextProvider
public interface ContextProviderWithLogLevelMethodOverriden extends VariableContext {

    @Var
    ContextProviderBadMethodNames info(long var);
}

import com.github.tantalor93.annotation.Var;
import com.github.tantalor93.annotation.VarContextProvider;
import com.github.tantalor93.VariableContext;

@VarContextProvider
public interface ContextProviderWithOverridenLogMethod extends VariableContext {

    @Var
    ContextProviderBadMethodNames log(long var);
}

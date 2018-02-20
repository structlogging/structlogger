import com.github.tantalor93.annotation.Var;
import com.github.tantalor93.annotation.VarContextProvider;
import com.github.tantalor93.VariableContext;

@VarContextProvider
public interface ContextProviderWithInvalidMethodName extends VariableContext {

    @Var
    ContextProviderWithInvalidMethodName varLong(long var);

    @Var
    ContextProviderWithInvalidMethodName warnEvent(String var);
}

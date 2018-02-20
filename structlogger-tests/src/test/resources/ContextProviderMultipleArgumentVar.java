import com.github.tantalor93.annotation.Var;
import com.github.tantalor93.annotation.VarContextProvider;
import com.github.tantalor93.VariableContext;

@VarContextProvider
public interface ContextProviderMultipleArgumentVar extends VariableContext {

    @Var
    ContextProviderMultipleArgumentVar varLong(long var, long var2);

    @Var
    ContextProviderMultipleArgumentVar varString(String var);

    @Var
    ContextProviderMultipleArgumentVar varInt(int var);

    @Var
    ContextProviderMultipleArgumentVar varDouble(double var);

    @Var
    ContextProviderMultipleArgumentVar varBoolean(boolean var);
}
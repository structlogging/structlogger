import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

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
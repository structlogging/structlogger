import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

@VarContextProvider
public interface ContextProviderBadReturnType extends VariableContext {

    @Var
    void varLong(long var);

    @Var
    ContextProviderBadReturnType varString(String var);

    @Var
    ContextProviderBadReturnType varInt(int var);

    @Var
    ContextProviderBadReturnType varDouble(double var);

    @Var
    ContextProviderBadReturnType varBoolean(boolean var);
}
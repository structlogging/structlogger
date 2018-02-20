import com.github.tantalor93.annotation.Var;
import com.github.tantalor93.annotation.VarContextProvider;
import com.github.tantalor93.VariableContext;

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
import com.github.tantalor93.annotation.Var;
import com.github.tantalor93.annotation.VarContextProvider;
import com.github.tantalor93.VariableContext;

@VarContextProvider
public interface ContextProviderNotExtending {

    @Var
    ContextProviderNotExtending varLong(long var);

    @Var
    ContextProviderNotExtending varString(String var);

    @Var
    ContextProviderNotExtending varInt(int var);

    @Var
    ContextProviderNotExtending varDouble(double var);

    @Var
    ContextProviderNotExtending varBoolean(boolean var);
}

import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

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

import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.VariableContext;

public interface ContextProviderNotAnnotated extends VariableContext {

    @Var
    ContextProviderNotAnnotated varLong(long var);

    @Var
    ContextProviderNotAnnotated varString(String var);

    @Var
    ContextProviderNotAnnotated varInt(int var);

    @Var
    ContextProviderNotAnnotated varDouble(double var);

    @Var
    ContextProviderNotAnnotated varBoolean(boolean var);
}

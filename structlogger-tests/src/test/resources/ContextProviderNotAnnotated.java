import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.VariableContext;

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

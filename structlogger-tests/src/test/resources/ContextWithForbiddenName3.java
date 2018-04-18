import com.github.structlogging.VariableContext;
import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;

@VarContextProvider
public interface ContextWithForbiddenName3 extends VariableContext {

    @Var
    ContextWithForbiddenName3 type(String context);

}
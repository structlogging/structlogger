import com.github.structlogging.VariableContext;
import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;

@VarContextProvider
public interface ContextWithForbiddenName extends VariableContext {

    @Var
    ContextWithForbiddenName context(String context);

}
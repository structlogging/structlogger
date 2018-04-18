import com.github.structlogging.VariableContext;
import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;

@VarContextProvider
public interface ContextWithForbiddenName2 extends VariableContext {

    @Var
    ContextWithForbiddenName2 timestamp(String context);

}
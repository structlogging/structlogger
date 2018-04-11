import com.github.structlogging.VariableContext;
import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;

@VarContextProvider
public interface AnotherContext extends VariableContext {

    @Var
    AnotherContext context(String context);

}

package cz.muni.fi.processor.utils;

import cz.muni.fi.annotation.VarContextProvider;

import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Class representing class annotated with {@link VarContextProvider}
 */
public class VariableContextProvider {

    private TypeMirror typeMirror;
    private List<Variable> variables;
    private boolean parametrization;

    public VariableContextProvider(final TypeMirror typeMirror, final List<Variable> variables, final boolean parametrization) {
        this.typeMirror = typeMirror;
        this.variables = variables;
        this.parametrization = parametrization;
    }

    /**
     *
     * @return TypeMirror of {@link VarContextProvider} annotated class
     */
    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    /**
     *
     * @return List of variables provided by {@link VarContextProvider} annotated class
     */
    public List<Variable> getVariables() {
        return variables;
    }

    /**
     *
     * @return whether this variable context provider uses parametrized log message
     */
    public boolean shouldParametrize() {
        return parametrization;
    }

    @Override
    public String toString() {
        return "VariableContextProvider{" +
                "typeMirror=" + typeMirror +
                ", variables=" + variables +
                '}';
    }
}

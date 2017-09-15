package org.ngmon.structlog.utils;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public class ProviderVariables {

    private TypeMirror typeMirror;
    private List<Variable> variables;


    public ProviderVariables(final TypeMirror typeMirror, final List<Variable> variables) {
        this.typeMirror = typeMirror;
        this.variables = variables;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "ProviderVariables{" +
                "typeMirror=" + typeMirror +
                ", variables=" + variables +
                '}';
    }
}

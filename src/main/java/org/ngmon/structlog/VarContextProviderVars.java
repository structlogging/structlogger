package org.ngmon.structlog;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class VarContextProviderVars {

    private TypeMirror typeMirror;
    private List<Element> variables;


    public VarContextProviderVars(final TypeMirror typeMirror, final List<Element> variables) {
        this.typeMirror = typeMirror;
        this.variables = variables;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public List<Element> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "VarContextProviderVars{" +
                "typeMirror=" + typeMirror +
                ", variables=" + variables +
                '}';
    }
}

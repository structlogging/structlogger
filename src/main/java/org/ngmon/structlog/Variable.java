package org.ngmon.structlog;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

public class Variable {

    private final Name name;

    private final TypeMirror type;

    public Variable(final Name name, final TypeMirror type) {
        this.name = name;
        this.type = type;
    }

    public Name getName() {
        return name;
    }

    public TypeMirror getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name=" + name +
                ", type=" + type +
                '}';
    }
}

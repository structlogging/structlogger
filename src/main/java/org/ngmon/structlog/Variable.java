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
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Variable variable = (Variable) o;

        if (name != null ? !name.equals(variable.name) : variable.name != null)
            return false;
        return type != null ? type.equals(variable.type) : variable.type == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name=" + name +
                ", type=" + type +
                '}';
    }
}
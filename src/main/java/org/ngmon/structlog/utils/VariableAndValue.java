package org.ngmon.structlog.utils;

import com.sun.source.tree.ExpressionTree;

public class VariableAndValue implements Comparable<VariableAndValue> {

    private final Variable variable;
    private final ExpressionTree value;

    public VariableAndValue(final Variable variable, final ExpressionTree value) {
        this.variable = variable;
        this.value = value;
    }

    public Variable getVariable() {
        return variable;
    }

    public ExpressionTree getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final VariableAndValue that = (VariableAndValue) o;

        return variable != null ? variable.equals(that.variable) : that.variable == null;
    }

    @Override
    public int hashCode() {
        return variable != null ? variable.hashCode() : 0;
    }

    @Override
    public int compareTo(final VariableAndValue o) {
        return this.variable.getName().toString().compareTo(o.getVariable().getName().toString());
    }

    @Override
    public String toString() {
        return "VariableAndValue{" +
                "variable=" + variable +
                ", value=" + value +
                '}';
    }
}

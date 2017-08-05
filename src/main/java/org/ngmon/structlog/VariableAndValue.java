package org.ngmon.structlog;

import com.sun.source.tree.ExpressionTree;

public class VariableAndValue {

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
    public String toString() {
        return "VariableAndValue{" +
                "variable=" + variable +
                ", value=" + value +
                '}';
    }
}

package org.ngmon.structlog.utils;

import com.sun.source.tree.ExpressionTree;

import javax.lang.model.element.Name;

public class MethodAndParameter {

    private final Name methodName;
    private final ExpressionTree parameter;

    public MethodAndParameter(final Name methodName, final ExpressionTree parameter) {
        this.methodName = methodName;
        this.parameter = parameter;
    }

    public Name getMethodName() {
        return methodName;
    }

    public ExpressionTree getParameter() {
        return parameter;
    }
}

package cz.muni.fi.utils;

import com.sun.source.tree.ExpressionTree;

import javax.lang.model.element.Name;

/**
 * Class representing method of {@link com.sun.source.tree.MethodInvocationTree}
 */
public class MethodAndParameter {

    private final Name methodName;
    private final ExpressionTree parameter; //parameter passed to method invocation

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

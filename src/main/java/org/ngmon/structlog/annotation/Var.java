package org.ngmon.structlog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying variables in {@link org.ngmon.structlog.VariableContext} interfaces
 * @see org.ngmon.structlog.DefaultContext for example
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface Var {}

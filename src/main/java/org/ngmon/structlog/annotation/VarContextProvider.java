package org.ngmon.structlog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying interfaces providing {@link Var} annotated methods
 * @see org.ngmon.structlog.DefaultContext for example
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface VarContextProvider {
}

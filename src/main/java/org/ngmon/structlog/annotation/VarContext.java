package org.ngmon.structlog.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VarContext {
    Class context() default void.class;
}

package org.ngmon.structlog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify {@link org.ngmon.structlog.StructLogger} fields
 * and specifying used {@link org.ngmon.structlog.VariableContext}
 *
 * Typical usage:
 * <code>
 * public class Example {
 *      @VarContext(context = DefaultContext.class)
 *      private static StructLogger<DefaultContext> defaultLog = StructLogger.instance();
 * }
 * </code>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
public @interface VarContext {
    Class context();
}

package cz.muni.fi.annotation;

import cz.muni.fi.VariableContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify {@link cz.muni.fi.StructLogger} fields
 * and specifying used {@link VariableContext} and name of logger (underlying slf4j logger)
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
public @interface LoggerContext {
    Class context();
}

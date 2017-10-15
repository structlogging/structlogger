package cz.muni.fi.annotation;

import cz.muni.fi.DefaultContext;
import cz.muni.fi.VariableContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying variables in {@link VariableContext} interfaces
 * @see DefaultContext for example
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface Var {}

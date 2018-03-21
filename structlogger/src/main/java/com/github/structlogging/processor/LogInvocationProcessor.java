/**
 * Copyright © 2018, Ondrej Benkovsky
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.github.structlogging.processor;

import static java.lang.String.format;

import com.github.structlogging.VariableContext;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.processor.utils.StructLoggerFieldContext;
import com.google.auto.service.AutoService;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.annotation.Var;
import com.github.structlogging.processor.exception.PackageNameException;
import com.github.structlogging.processor.utils.GeneratedClassInfo;
import com.github.structlogging.processor.utils.ScannerParams;
import com.github.structlogging.processor.utils.Variable;
import com.github.structlogging.processor.utils.VariableContextProvider;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Main annotation processor of structlogger, takes care of locating {@link VarContextProvider} annotated classes, {@link LoggerContext} annotated StructLogger fields
 * and takes care of replacing all valid structured log statements with generated structured log events invocations
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class LogInvocationProcessor extends AbstractProcessor {


    /**
     * Set of all classes annotated with {@link VarContextProvider}, set of all classes which can provide variable logging context
     */
    private final Set<TypeMirror> varContextProviders = new HashSet<>();

    /**
     * Map representing for all {@link VarContextProvider} annotated classes, what kind of variables they expose (all {@link Var} annotated elements
     */
    private final Map<TypeMirror, VariableContextProvider> varsHashMap = new HashMap<>();

    /**
     * Set of all generated classes (logging events), used by {@link SchemaGenerator}
     */
    private final Set<GeneratedClassInfo> generatedClassesInfo = new HashSet<>();

    private Trees trees;
    private Messager messager;
    private Types types;

    private LogInvocationScanner logInvocationScanner;

    private boolean initFailed = false; //flag that init method has errors

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        messager = processingEnv.getMessager();
        types = processingEnv.getTypeUtils();

        try {
            logInvocationScanner = new LogInvocationScanner(
                    processingEnv
            );
        } catch (IOException e) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "IOException caught"
            );
            initFailed = true;
        } catch (PackageNameException e) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "generatedEventsPackage compiler argument is not valid, either it contains java keyword or subpackage or class name starts with number"
            );
            initFailed = true;
        }

        final String schemasRoot = processingEnv.getOptions().get("schemasRoot");
        if (schemasRoot != null) {

            // Used for generating json schemas by {@link SchemaGenerator}
            SchemaGenerator schemaGenerator = new SchemaGenerator(generatedClassesInfo, schemasRoot);
            JavacTask.instance(processingEnv).addTaskListener(schemaGenerator);
        }
        else {
            messager.printMessage(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    "schemasRoot compiler argument is not set, no schemas will be created"
            );
        }
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        if (initFailed) {
            return false;
        }
        // process every class to be compiled, locate all StructLogger fields annotated with LoggerContext annotation, find all usages in given file and replace
        // it with generated event class
        processStructLogExpressions(roundEnv);

        // do not claim ownership of any annotation
        return false;
    }

    /**
     * checks VarContextProvider, whether it is interface, extends VariableContext, is properly annotated, contains proper logging variable methods
     * @param typeMirror of VarContextProvider
     * @return false when VarContextProvider is not valid
     */
    private boolean checkVarContextProvider(final TypeMirror typeMirror) {
        final TypeElement element = (TypeElement) types.asElement(typeMirror);
        if (varContextProviders.contains(typeMirror)) { // this VarContextProvider is already processed
            return true;
        }

        if (!element.getKind().isInterface()) { //check whether class is interface
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    format(
                            "%s should be interface",
                            element
                    ),
                    element
            );
            return false;
        }

        boolean extendsVariableContext = extendsVariableContext(element);

        if (!extendsVariableContext) { //check whether interface extends VariableContext
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    format(
                            "%s should be extending %s",
                            element,
                            VariableContext.class.getName()
                    ),
                    element
            );
            return false;
        }

        final List<Variable> elements = new ArrayList<>();
        final VarContextProvider varContextProvider = element.getAnnotation(VarContextProvider.class);

        if (varContextProvider == null) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    format(
                            "%s should be annotated with @VarContextProvider",
                            element
                    ),
                    element
            );
            return false;
        }

        //check methods of interface
        for (Element enclosed : element.getEnclosedElements()) {
            final Var annotation = enclosed.getAnnotation(Var.class);
            if (annotation != null) {
                final ExecutableType executableType = (ExecutableType) enclosed.asType();
                final Name simpleName = enclosed.getSimpleName();
                // check name of method

                //should not have method with names info, debug, error,...
                final List<String> logLevelsMethodNames = Arrays.stream(
                        LogLevel.values()
                )
                        .map(
                                LogLevel::getLevelMethodName
                        )
                        .collect(
                                Collectors.toList()
                        );

                //should not have method with names infoEvent, debugEvent, errorEvent,...
                final List<String> logEventMethodNames = Arrays.stream(
                        LogLevel.values()
                )
                        .map(
                                LogLevel::getLogEventMethodName
                        )
                        .collect(
                                Collectors.toList()
                        );
                if (
                        simpleName.contentEquals("log") || //should not have method with name log
                        logLevelsMethodNames.stream().anyMatch(simpleName::contentEquals) ||
                        logEventMethodNames.stream().anyMatch(simpleName::contentEquals)
                   )
                {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            format(
                                    "%s interface cannot have method named %s",
                                    element,
                                    simpleName
                            ),
                            element
                    );
                    return false;
                }
                if (!executableType.getReturnType().toString().equals(typeMirror.toString())) { //check return type
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            format(
                                    "%s.%s method must have return type %s",
                                    element,
                                    simpleName,
                                    element
                            ),
                            element
                    );
                    return false;
                }
                if (executableType.getParameterTypes().size() != 1) { //check number of parameters
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            format(
                                    "%s.%s method must have exactly one argument",
                                    element,
                                    simpleName
                            ),
                            element);
                    return false;
                }
                if (elements.stream().map(Variable::getName).anyMatch(e -> e.contentEquals(simpleName))) { //check whether there is no method with same name
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            format(
                                    "%s.%s method cannot be overloaded",
                                    element,
                                    simpleName
                            ),
                            element
                    );
                    return false;
                }
                elements.add(new Variable(simpleName, executableType.getParameterTypes().get(0)));
            }
        }
        if (elements.isEmpty()) {
            messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    format(
                            "%s has no @Var annotated methods",
                            element
                    ),
                    element
            );
        }
        varsHashMap.put(typeMirror, new VariableContextProvider(typeMirror, elements, varContextProvider.parametrization()));
        varContextProviders.add(typeMirror);
        return true;
    }

    /**
     * Check whether type element extends {@link VariableContext}
     */
    private boolean extendsVariableContext(final TypeElement typeElement) {
        boolean extendsVariableContext = false;
        for (TypeMirror extendingInterface : typeElement.getInterfaces()) {
            if (extendingInterface.toString().equals(VariableContext.class.getCanonicalName())) {
                extendsVariableContext = true;
            }
        }
        return extendsVariableContext;
    }

    /**
     * Find all classes, which have some fields annotated with {@link LoggerContext} and call {@link LogInvocationScanner} for given class if class indeed
     * have such fields
     */
    private void processStructLogExpressions(final RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getRootElements()) {
            final Map<Name, StructLoggerFieldContext> fields = new HashMap<>();

            for (Element enclosed : element.getEnclosedElements()) {
                if (enclosed.getKind().isField()) {
                    try {
                        final LoggerContext annotation = enclosed.getAnnotation(LoggerContext.class);
                        if (annotation != null) {
                            annotation.context();
                            //TODO class is already compiled
                        }
                    } catch (MirroredTypeException ex) {
                        final TypeMirror contextProviderTypeMirror = ex.getTypeMirror();
                        if (!checkVarContextProvider(contextProviderTypeMirror)) {
                            return;
                        }
                        fields.put(enclosed.getSimpleName(), new StructLoggerFieldContext(contextProviderTypeMirror));
                    }
                }
            }

            final TypeElement typeElement = (TypeElement) element;
            final TreePath path = trees.getPath(element);

            // do not do any code replacement in such class which do not specify any LoggerContext annotated StructLogger
            if (!fields.isEmpty()) {
                logInvocationScanner.scan(
                        path,
                        new ScannerParams(
                                typeElement,
                                path.getCompilationUnit(),
                                varsHashMap,
                                fields,
                                generatedClassesInfo
                        )
                );

            }
        }
    }
}

package cz.muni.fi.processor;

import static java.lang.String.format;

import com.google.auto.service.AutoService;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import cz.muni.fi.VariableContext;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.processor.exception.PackageNameException;
import cz.muni.fi.processor.utils.GeneratedClassInfo;
import cz.muni.fi.processor.utils.ScannerParams;
import cz.muni.fi.processor.utils.StructLoggerFieldContext;
import cz.muni.fi.processor.utils.Variable;
import cz.muni.fi.processor.utils.VariableContextProvider;

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
 * and takes care of replacing all valid structured log statements with generated structured log events invocations of slf4j logging API
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
    private final HashMap<TypeMirror, VariableContextProvider> varsHashMap = new HashMap<>();

    /**
     * Set of all generated classes (logging events), used by {@link SchemaGenerator}
     */
    private final Set<GeneratedClassInfo> generatedClassesInfo = new HashSet<>();

    private Trees trees;
    private Messager messager;
    private Types types;

    /**
     * Used for generating json schemas by {@link SchemaGenerator}
     */
    private SchemaGenerator schemaGenerator;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        messager = processingEnv.getMessager();
        types = processingEnv.getTypeUtils();

        final String schemasRoot = processingEnv.getOptions().get("schemasRoot");
        if (schemasRoot != null) {
            schemaGenerator = new SchemaGenerator(generatedClassesInfo, schemasRoot);
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
        // process every class to be compiled, locate all StructLogger fields annotated with LoggerContext annotation, find all usages in given file and replace
        // it with generated event class
        processStructLogExpressions(roundEnv);

        // do not claim ownership of any annotation
        return false;
    }

    /**
     * checks VarContextProvider
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

        for (Element enclosed : element.getEnclosedElements()) {
            final Var annotation = enclosed.getAnnotation(Var.class);
            if (annotation != null) {
                final ExecutableType executableType = (ExecutableType) enclosed.asType();
                final Name simpleName = enclosed.getSimpleName();
                // check name of method
                final List<String> logLevelsMethodNames = Arrays.asList(
                        LogLevel.values()
                ).stream()
                        .map(
                                e -> e.getLevelMethodName()
                        )
                        .collect(
                                Collectors.toList()
                        );

                final List<String> logEventMethodNames = Arrays.asList(
                        LogLevel.values()
                ).stream()
                        .map(
                                e -> e.getLogEventMethodName()
                        )
                        .collect(
                                Collectors.toList()
                        );
                if (
                        simpleName.contentEquals("log") ||
                        logLevelsMethodNames.stream().anyMatch(e -> simpleName.contentEquals(e)) ||
                        logEventMethodNames.stream().anyMatch(e -> simpleName.contentEquals(e))
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
                try {
                    new LogInvocationScanner(
                            varsHashMap,
                            fields,
                            processingEnv,
                            generatedClassesInfo
                    ).scan(
                            path,
                            new ScannerParams(
                                    typeElement,
                                    path.getCompilationUnit()
                            )
                    );
                } catch (IOException e) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "IOException caught"
                    );
                } catch (PackageNameException e) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "generatedEventsPackage compiler argument is not valid, either it contains java keyword or subpackage or class name starts with number"
                    );
                }
            }
        }
    }
}

package cz.muni.fi.processor;

import static java.lang.String.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.auto.service.AutoService;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import cz.muni.fi.VariableContext;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;
import cz.muni.fi.utils.GeneratedClassInfo;
import cz.muni.fi.utils.ProviderVariables;
import cz.muni.fi.utils.ScannerParams;
import cz.muni.fi.utils.StructLoggerFieldContext;
import cz.muni.fi.utils.Variable;
import org.reflections.Reflections;

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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    private final HashMap<TypeMirror, ProviderVariables> varsHashMap = new HashMap<>();

    /**
     * Set of all generated classes (logging events), used by {@link SchemaGenerator}
     */
    private final Set<GeneratedClassInfo> generatedClassesInfo = new HashSet<>();

    private Trees trees;
    private Messager messager;
    private Elements elements;

    /**
     * Used for generating json schemas by {@link SchemaGenerator}
     */
    private SchemaGenerator schemaGenerator;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //TODO check that schemasRoot is set
        schemaGenerator = new SchemaGenerator(generatedClassesInfo, processingEnv.getOptions().get("schemasRoot"));

        JavacTask.instance(processingEnv).addTaskListener(schemaGenerator);

        trees = Trees.instance(processingEnv);
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {

        // process all classes annotated with @VarContextProvider either using reflection API or compiler API
        processVariableContextClasses(roundEnv);

        // process every class to be compiled, locate all StructLogger fields annotated with LoggerContext annotation, find all usages in given file and replace
        // it with generated event class
        processStructLogExpressions(roundEnv);

        // do not claim ownership of any annotation
        return false;
    }

    /**
     * find all classes annotated with {@link VarContextProvider} and check whether they are valid variable context providers
     */
    private void processVariableContextClasses(final RoundEnvironment roundEnv) {

        try { //use reflection here to get already compiled classes from dependencies
            final Reflections reflections = new Reflections();
            final Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(VarContextProvider.class);
            for (Class<?> c : typesAnnotatedWith) {
                boolean extendsVariableContext = extendsVariableContext(c);
                if (!extendsVariableContext) {
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s should be extending %s", c.getName(), VariableContext.class.getName()));
                    return;
                }

                final TypeElement typeElement = elements.getTypeElement(c.getCanonicalName());
                if (processElement(typeElement, typeElement.asType()))
                    return;
            }
        } catch (Exception ex) {
        }

        //use compiler api and annotation api here to get classes yet to be compiled
        for (Element element : roundEnv.getElementsAnnotatedWith(VarContextProvider.class)) {
            if (!element.getKind().isInterface()) { //check whether class is interface
                messager.printMessage(Diagnostic.Kind.ERROR, format("%s should be interface", element), element);
                return;
            }
            final TypeMirror typeMirror = element.asType();
            final TypeElement typeElement = (TypeElement) element;
            boolean extendsVariableContext = extendsVariableContext(typeElement);
            if (!extendsVariableContext) { //check whether interface extends VariableContext
                messager.printMessage(Diagnostic.Kind.ERROR, format("%s should be extending %s", element, VariableContext.class.getName()), element);
                return;
            }
            if (processElement(element, typeMirror))
                return;
        }
    }

    private boolean processElement(final Element element, final TypeMirror typeMirror) {
        varContextProviders.add(typeMirror);
        final List<Variable> elements = new ArrayList<>();
        for (Element enclosed : element.getEnclosedElements()) {
            final Var annotation = enclosed.getAnnotation(Var.class);
            if (annotation != null) {
                final ExecutableType executableType = (ExecutableType) enclosed.asType();
                final Name simpleName = enclosed.getSimpleName();
                if (simpleName.contentEquals("log") || // check name of method
                        simpleName.contentEquals("info") ||
                        simpleName.contentEquals("error") ||
                        simpleName.contentEquals("warn") ||
                        simpleName.contentEquals("debug") ||
                        simpleName.contentEquals("message") ||
                        simpleName.contentEquals("level")) {
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s interface cannot have method named %s", element, simpleName), element);
                    return true;
                }
                if (!executableType.getReturnType().toString().equals(typeMirror.toString())) { //check return type
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s.%s method must have return type %s", element, simpleName, element), element);
                    return true;
                }
                if (executableType.getParameterTypes().size() != 1) { //check number of parameters
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s.%s method must have exactly one argument", element, simpleName), element);
                    return true;
                }
                if (elements.stream().map(e -> e.getName()).anyMatch(e -> e.contentEquals(simpleName))) { //check whether there is no method with same name
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s.%s method cannot be overloaded", element, simpleName), element);
                    return true;
                }
                elements.add(new Variable(simpleName, executableType.getParameterTypes().get(0)));
            }
        }
        if (elements.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.WARNING, format("%s has no @Var annotated methods", element), element);
        }
        varsHashMap.put(typeMirror, new ProviderVariables(typeMirror, elements));
        return false;
    }

    /**
     * Check whether type element extends {@link VariableContext}
     */
    private boolean extendsVariableContext(final TypeElement typeElement) {
        boolean extendsVariableContext = false;
        for (TypeMirror extendingInterfaces : typeElement.getInterfaces()) {
            if (extendingInterfaces.equals(elements.getTypeElement(VariableContext.class.getCanonicalName()).asType())) {
                extendsVariableContext = true;
            }
        }
        return extendsVariableContext;
    }

    /**
     * Check whether class extends {@link VariableContext}
     */
    private boolean extendsVariableContext(final Class<?> c) {
        final Class<?>[] interfaces = c.getInterfaces();
        boolean extendsVariableContext = false;
        for (Class intf : interfaces) {
            if (intf.getCanonicalName().equals(VariableContext.class.getCanonicalName())) {
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
                        final TypeMirror typeMirror = ex.getTypeMirror();

                        if (varContextProviders.contains(typeMirror)) {
                            fields.put(enclosed.getSimpleName(), new StructLoggerFieldContext(typeMirror));
                        }
                    }
                }
            }

            final TypeElement typeElement = (TypeElement) element;
            final TreePath path = trees.getPath(element);

            // do not generate logger fields for classes which do not specify any LoggerContext annotated StructLogger
            // and do not do any code replacement in such class
            if (!fields.isEmpty()) {
                new LogInvocationScanner(varsHashMap, fields, processingEnv, generatedClassesInfo).scan(path, new ScannerParams(typeElement, path.getCompilationUnit()));
            }
        }
    }
}

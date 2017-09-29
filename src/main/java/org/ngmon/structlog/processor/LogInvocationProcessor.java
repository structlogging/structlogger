package org.ngmon.structlog.processor;

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
import org.ngmon.structlog.utils.GeneratedClassInfo;
import org.ngmon.structlog.utils.ProviderVariables;
import org.ngmon.structlog.utils.ScannerParams;
import org.ngmon.structlog.utils.Variable;
import org.ngmon.structlog.VariableContext;
import org.ngmon.structlog.annotation.Var;
import org.ngmon.structlog.annotation.VarContext;
import org.ngmon.structlog.annotation.VarContextProvider;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Main annotation processor of structlogger, takes care of locating {@link VarContextProvider} annotated classes, {@link VarContext} annotated StructLogger fields
 * and takes care of replacing all valid structured log statements with generated structured log events invocations of slf4j logging API
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class LogInvocationProcessor extends AbstractProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);

    private final Set<TypeMirror> varContextProviders = new HashSet<>();
    private final HashMap<TypeMirror, ProviderVariables> varsHashMap = new HashMap<>();
    private final Set<GeneratedClassInfo> generatedClassesInfo = new HashSet<>();

    private Trees trees;
    private Messager messager;
    private Elements elements;

    private final SchemaGenerator schemaGenerator = new SchemaGenerator();

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        JavacTask.instance(processingEnv).addTaskListener(schemaGenerator);

        trees = Trees.instance(processingEnv);
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {

        processVariableContextClasses(roundEnv);

        processStructLogExpressions(roundEnv);

        return false;
    }

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
            if (!element.getKind().isInterface()) {
                messager.printMessage(Diagnostic.Kind.ERROR, format("%s should be interface", element), element);
                return;
            }
            final TypeMirror typeMirror = element.asType();
            final TypeElement typeElement = (TypeElement) element;
            boolean extendsVariableContext = extendsVariableContext(typeElement);
            if (!extendsVariableContext) {
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
                if (simpleName.contentEquals("log") ||
                        simpleName.contentEquals("info") ||
                        simpleName.contentEquals("error") ||
                        simpleName.contentEquals("warn") ||
                        simpleName.contentEquals("debug") ||
                        simpleName.contentEquals("message") ||
                        simpleName.contentEquals("level")) {
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s interface cannot have method named %s", element, simpleName), element);
                    return true;
                }
                if (!executableType.getReturnType().toString().equals(typeMirror.toString())) {
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s.%s method must have return type %s", element, simpleName, element), element);
                    return true;
                }
                if (executableType.getParameterTypes().size() != 1) {
                    messager.printMessage(Diagnostic.Kind.ERROR, format("%s.%s method must have exactly one argument", element, simpleName), element);
                    return true;
                }
                if (elements.stream().map(e -> e.getName()).anyMatch(e -> e.contentEquals(simpleName))) {
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

    private boolean extendsVariableContext(final TypeElement typeElement) {
        boolean extendsVariableContext = false;
        for (TypeMirror extendingInterfaces : typeElement.getInterfaces()) {
            if (extendingInterfaces.equals(elements.getTypeElement(VariableContext.class.getCanonicalName()).asType())) {
                extendsVariableContext = true;
            }
        }
        return extendsVariableContext;
    }

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

    private void processStructLogExpressions(final RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getRootElements()) {
            final Map<Name, TypeMirror> fields = new HashMap<>();

            for (Element enclosed : element.getEnclosedElements()) {
                if (enclosed.getKind().isField()) {
                    try {
                        final VarContext annotation = enclosed.getAnnotation(VarContext.class);
                        if (annotation != null) {
                            annotation.context();
                            //TODO class is already compiled
                        }
                    } catch (MirroredTypeException ex) {
                        final TypeMirror typeMirror = ex.getTypeMirror();

                        if (varContextProviders.contains(typeMirror)) {
                            fields.put(enclosed.getSimpleName(), typeMirror);
                        }
                    }
                }
            }

            final TypeElement typeElement = (TypeElement) element;
            final TreePath path = trees.getPath(element);

            // do not generate logger fields for classes which do not specify any VarContext annotated StructLogger
            // and do not do any code replacement in such class
            if (!fields.isEmpty()) {
                new LogInvocationScanner(varsHashMap, fields, processingEnv, generatedClassesInfo).scan(path, new ScannerParams(typeElement, path.getCompilationUnit()));
            }
        }
    }

    private final class SchemaGenerator implements TaskListener {
        @Override
        public void started(final TaskEvent e) {

        }

        @Override
        public void finished(final TaskEvent e) {

            if (e.getKind() != TaskEvent.Kind.GENERATE) {
                return;
            }

            final Iterator<GeneratedClassInfo> iterator = generatedClassesInfo.iterator();
            while (iterator.hasNext()) {
                final GeneratedClassInfo generatedGeneratedClassInfo = iterator.next();
                try {
                    final Class<?> clazz = Class.forName(generatedGeneratedClassInfo.getQualifiedName());
                    final JsonSchema schema = schemaGen.generateSchema(clazz);
                    schema.set$schema("http://json-schema.org/draft-03/schema#");
                    schema.setDescription(generatedGeneratedClassInfo.getDescription());
                    schema.asObjectSchema().setTitle(generatedGeneratedClassInfo.getSimpleName());
                    iterator.remove();
                    createSchemaFile("events", generatedGeneratedClassInfo.getSimpleName(), schema);
                } catch (Exception e1) {
                    //TODO
                }
            }
        }
    }

    private void createSchemaFile(String namespace, String signature, JsonSchema schema) {
        try {
            String dir = "schemas/" + namespace.replace(".", "/") + "/";
            Files.createDirectories(Paths.get(dir));
            FileOutputStream out = new FileOutputStream(dir + signature + ".json");
            out.write(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(schema));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

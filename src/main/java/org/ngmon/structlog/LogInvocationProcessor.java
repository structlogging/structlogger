package org.ngmon.structlog;

import static java.lang.String.format;

import com.google.auto.service.AutoService;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import org.ngmon.structlog.annotation.Var;
import org.ngmon.structlog.annotation.VarContext;
import org.ngmon.structlog.annotation.VarContextProvider;

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
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class LogInvocationProcessor extends AbstractProcessor {

    private final Set<TypeMirror> varContextProviders = new HashSet<>();
    private final HashMap<TypeMirror, ProviderVariables> varsHashMap = new HashMap<>();

    private Trees trees;
    private Messager messager;
    private Elements elements;
    private Types types;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(VarContextProvider.class)) {
            if (!element.getKind().isInterface()) {
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, format("%s should be interface", element));
                continue;
            }
            final TypeMirror typeMirror = element.asType();
            final TypeElement typeElement = (TypeElement) element;
            boolean extendsVariableContext = false;
            for (TypeMirror extendingInterfaces : typeElement.getInterfaces()) {
                if (extendingInterfaces.equals(elements.getTypeElement(VariableContext.class.getCanonicalName()).asType())) {
                    extendsVariableContext = true;
                }
            }
            if (!extendsVariableContext) {
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, format("%s should be extending %s", element, VariableContext.class.getName()));
                continue;
            }
            varContextProviders.add(typeMirror);
            final List<Variable> elements = new ArrayList<>();
            for (Element enclosed : element.getEnclosedElements()) {
                final Var annotation = enclosed.getAnnotation(Var.class);
                if (annotation != null) {
                    final ExecutableType executableType = (ExecutableType) enclosed.asType();
                    elements.add(new Variable(enclosed.getSimpleName(), executableType.getParameterTypes().get(0)));
                }
            }
            if (elements.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.WARNING, format("%s has no @Var annotated methods", element));
            }
            varsHashMap.put(typeMirror, new ProviderVariables(typeMirror, elements));
        }

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

            final TreePath path = trees.getPath(element);
            new LogInvocationScanner(varsHashMap, fields, processingEnv).scan(path, path.getCompilationUnit());
        }


        return false;
    }
}

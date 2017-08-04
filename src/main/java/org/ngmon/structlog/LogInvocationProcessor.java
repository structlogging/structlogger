package org.ngmon.structlog;

import com.google.auto.service.AutoService;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import org.ngmon.structlog.annotation.Var;
import org.ngmon.structlog.annotation.VarContext;
import org.ngmon.structlog.annotation.VarContextProvider;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
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
    private final HashMap<TypeMirror, VarContextProviderVars> varsHashMap = new HashMap<>();

    private Trees trees;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(VarContextProvider.class)) {
            final TypeMirror typeMirror = element.asType();

            varContextProviders.add(typeMirror);
            final List<Element> elements = new ArrayList<>();
            for (Element enclosed : element.getEnclosedElements()) {
                Var annotation = enclosed.getAnnotation(Var.class);
                if (annotation != null) {
                    elements.add(enclosed);
                }
            }
            varsHashMap.put(typeMirror, new VarContextProviderVars(typeMirror, elements));
        }

        for (Element element : roundEnv.getRootElements()) {
            final Map<Name, TypeMirror> fields = new HashMap<>();

            for (Element enclosed : element.getEnclosedElements()) {
                if (enclosed.getKind().isField()) {
                    try {

                        final VarContext annotation = enclosed.getAnnotation(VarContext.class);
                        if (annotation != null) {
                            annotation.context();
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
            new LogInvocationScanner(varsHashMap, fields).scan(path, path.getCompilationUnit());
        }


        return false;
    }
}

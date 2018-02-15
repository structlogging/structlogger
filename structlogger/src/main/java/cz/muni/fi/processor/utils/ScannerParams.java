package cz.muni.fi.processor.utils;

import com.sun.source.tree.CompilationUnitTree;
import cz.muni.fi.processor.LogInvocationScanner;

import javax.lang.model.element.TypeElement;

/**
 * Class representing parameters passed to {@link LogInvocationScanner}
 */
public class ScannerParams {

    private final TypeElement typeElement;
    private final CompilationUnitTree compilationUnitTree;

    public ScannerParams(final TypeElement typeElement, final CompilationUnitTree compilationUnitTree) {
        this.typeElement = typeElement;
        this.compilationUnitTree = compilationUnitTree;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public CompilationUnitTree getCompilationUnitTree() {
        return compilationUnitTree;
    }
}

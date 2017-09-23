package org.ngmon.structlog.utils;

import com.sun.source.tree.CompilationUnitTree;

import javax.lang.model.element.TypeElement;

/**
 * Class representing parameters passed to {@link org.ngmon.structlog.processor.LogInvocationScanner}
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

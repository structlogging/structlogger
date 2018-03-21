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
package com.github.structlogging.processor.utils;

import com.github.structlogging.annotation.Var;
import com.github.structlogging.annotation.VarContextProvider;
import com.github.structlogging.processor.SchemaGenerator;
import com.sun.source.tree.CompilationUnitTree;
import com.github.structlogging.processor.LogInvocationScanner;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.Set;

/**
 * Class representing parameters passed to {@link LogInvocationScanner}
 */
public class ScannerParams {

    private final TypeElement typeElement; //type which is being scanned
    private final CompilationUnitTree compilationUnitTree;

    /**
     * Map representing for all {@link VarContextProvider} annotated classes, what kind of variables they expose (all {@link Var} annotated elements
     */
    private final Map<TypeMirror, VariableContextProvider> varsHashMap;
    private final Map<Name, StructLoggerFieldContext> fields;

    /**
     * Set of all generated classes (logging events), this set is continuously filled by {@link LogInvocationScanner} and when scanning of all compiled events is done
     * this set is passed to {@link SchemaGenerator}
     */
    private final Set<GeneratedClassInfo> generatedClassesInfo;

    public ScannerParams(final TypeElement typeElement,
                         final CompilationUnitTree compilationUnitTree,
                         final Map<TypeMirror, VariableContextProvider> varsHashMap,
                         final Map<Name, StructLoggerFieldContext> fields,
                         final Set<GeneratedClassInfo> generatedClassesInfo) {
        this.typeElement = typeElement;
        this.compilationUnitTree = compilationUnitTree;
        this.varsHashMap = varsHashMap;
        this.fields = fields;
        this.generatedClassesInfo = generatedClassesInfo;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public CompilationUnitTree getCompilationUnitTree() {
        return compilationUnitTree;
    }

    public Map<TypeMirror, VariableContextProvider> getVarsHashMap() {
        return varsHashMap;
    }

    public Map<Name, StructLoggerFieldContext> getFields() {
        return fields;
    }

    public Set<GeneratedClassInfo> getGeneratedClassesInfo() {
        return generatedClassesInfo;
    }
}

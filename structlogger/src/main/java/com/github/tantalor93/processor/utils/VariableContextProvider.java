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
package com.github.tantalor93.processor.utils;

import com.github.tantalor93.annotation.VarContextProvider;

import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Class representing class annotated with {@link VarContextProvider}
 */
public class VariableContextProvider {

    private TypeMirror typeMirror;
    private List<Variable> variables;
    private boolean parametrization;

    public VariableContextProvider(final TypeMirror typeMirror, final List<Variable> variables, final boolean parametrization) {
        this.typeMirror = typeMirror;
        this.variables = variables;
        this.parametrization = parametrization;
    }

    /**
     *
     * @return TypeMirror of {@link VarContextProvider} annotated class
     */
    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    /**
     *
     * @return List of variables provided by {@link VarContextProvider} annotated class
     */
    public List<Variable> getVariables() {
        return variables;
    }

    /**
     *
     * @return whether this variable context provider uses parametrized log message
     */
    public boolean shouldParametrize() {
        return parametrization;
    }

    @Override
    public String toString() {
        return "VariableContextProvider{" +
                "typeMirror=" + typeMirror +
                ", variables=" + variables +
                '}';
    }
}

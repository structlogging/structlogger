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

import java.util.List;

/**
 * Class containing info about generated POJO for structured log event
 */
public class GeneratedClassInfo {

    private final String qualifiedName;
    private final String simpleName;
    private final String description;
    private final List<VariableAndValue> usedVariables;
    private final String packageName;

    public GeneratedClassInfo(final String qualifiedName,
                              final String simpleName,
                              final String description,
                              final List<VariableAndValue> usedVariables,
                              final String packageName) {
        this.qualifiedName = qualifiedName;
        this.simpleName = simpleName;
        this.description = description;
        this.usedVariables = usedVariables;
        this.packageName = packageName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getDescription() {
        return description;
    }

    public List<VariableAndValue> getUsedVariables() {
        return usedVariables;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final GeneratedClassInfo generatedClassInfo = (GeneratedClassInfo) o;

        return qualifiedName != null ? qualifiedName.equals(generatedClassInfo.qualifiedName) : generatedClassInfo.qualifiedName == null;
    }

    @Override
    public int hashCode() {
        return qualifiedName != null ? qualifiedName.hashCode() : 0;
    }
}

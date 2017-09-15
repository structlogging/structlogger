package org.ngmon.structlog.utils;

import java.util.List;

public class GeneratedClassInfo {

    private final String qualifiedName;
    private final String simpleName;
    private final String description;
    private final List<VariableAndValue> usedVariables;

    public GeneratedClassInfo(final String qualifiedName,
                              final String simpleName,
                              final String description,
                              final List<VariableAndValue> usedVariables) {
        this.qualifiedName = qualifiedName;
        this.simpleName = simpleName;
        this.description = description;
        this.usedVariables = usedVariables;
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

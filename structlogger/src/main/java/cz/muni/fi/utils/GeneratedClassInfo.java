package cz.muni.fi.utils;

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

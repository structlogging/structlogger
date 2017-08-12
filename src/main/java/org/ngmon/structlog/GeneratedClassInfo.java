package org.ngmon.structlog;

public class GeneratedClassInfo {

    private final String qualified;
    private final String simple;
    private final String description;

    public GeneratedClassInfo(final String qualified, final String simple, final String description) {
        this.qualified = qualified;
        this.simple = simple;
        this.description = description;
    }

    public String getQualified() {
        return qualified;
    }

    public String getSimple() {
        return simple;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final GeneratedClassInfo generatedClassInfo = (GeneratedClassInfo) o;

        return qualified != null ? qualified.equals(generatedClassInfo.qualified) : generatedClassInfo.qualified == null;
    }

    @Override
    public int hashCode() {
        return qualified != null ? qualified.hashCode() : 0;
    }
}

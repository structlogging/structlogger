package org.ngmon.structlog;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.tree.JCTree;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.util.SortedSet;

public class POJOService {

    static final String PACKAGE_NAME = "compilerproject.generated";

    private final Filer filer;

    public POJOService(final Filer filer) {
        this.filer = filer;
    }

    public String createPojo(final JCTree.JCLiteral literal,
                           final String level,
                           final SortedSet<VariableAndValue> usedVariables) {

        final String eventName = "Event" + hash(literal.getValue().toString());

        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(eventName).addModifiers(Modifier.PUBLIC);
        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

        addPojoField(classBuilder, constructorBuilder, "level", TypeName.get(String.class));
        addPojoField(classBuilder, constructorBuilder, "message", TypeName.get(String.class));

        for(VariableAndValue variableAndValue : usedVariables) {
            addPojoField(classBuilder, constructorBuilder, variableAndValue.getVariable().getName().toString(), TypeName.get(variableAndValue.getVariable().getType()));
        }

        final TypeSpec build = classBuilder.addMethod(constructorBuilder.build()).build();

        final JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, build).build();

        try {
            javaFile.writeTo(filer);
        } catch (Exception ignored) {
        }
        return eventName;
    }

    private void addPojoField(final TypeSpec.Builder classBuilder,
                              final MethodSpec.Builder constructorBuilder,
                              final String fieldName,
                              final TypeName fieldClass) {
        classBuilder.addField(fieldClass, fieldName, Modifier.PRIVATE, Modifier.FINAL);
        addGetter(classBuilder, fieldName, fieldClass);
        addConstructorParameter(constructorBuilder, fieldName, fieldClass);
    }

    private void addConstructorParameter(final MethodSpec.Builder constructorBuilder, final String attributeName, final TypeName type) {
        constructorBuilder.addParameter(type, attributeName, Modifier.FINAL);
        constructorBuilder.addCode("this." + attributeName + "=" + attributeName + ";");
    }

    private void addGetter(final TypeSpec.Builder classBuilder, final String attributeName, final TypeName type) {
        final String getterMethodName = "get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
        final MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(getterMethodName);
        getterBuilder.returns(type);
        getterBuilder.addModifiers(Modifier.PUBLIC);
        getterBuilder.addCode("return this." + attributeName + ";");

        classBuilder.addMethod(getterBuilder.build());
    }

    private String hash(String string) {
        final String sha1Hex = DigestUtils.sha1Hex(string);
        return StringUtils.substring(sha1Hex, 0, 8);
    }
}

package cz.muni.fi.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.tree.JCTree;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import cz.muni.fi.utils.VariableAndValue;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * Service for creating POJO and generating them during annotation processing
 */
public class POJOService {

    public static final String PACKAGE_NAME = "structlogger.generated";

    private final Filer filer;

    public POJOService(final Filer filer) {
        this.filer = filer;
    }

    /**
     * Create JavaFile representing POJO based on String literal and usedVariables of log statement
     *
     * @param literal String literal used in structured log statement
     * @param usedVariables list of logging variables used by structured log statement
     * @return JavaFile representing Structured log Event (this JavaFile is not yet written, @see POJOService.writeJavaFile)
     */
    public JavaFile createPojo(final JCTree.JCLiteral literal,
                               final List<VariableAndValue> usedVariables) {

        final String eventName = "Event" + hash(literal.getValue().toString());

        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(eventName).addModifiers(Modifier.PUBLIC);
        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

        addPojoField(classBuilder, constructorBuilder, "message", TypeName.get(String.class));
        addPojoField(classBuilder, constructorBuilder, "sourceFile", TypeName.get(String.class));
        addPojoField(classBuilder, constructorBuilder, "lineNumber", TypeName.LONG);

        for (VariableAndValue variableAndValue : usedVariables) {
            addPojoField(classBuilder, constructorBuilder, variableAndValue.getVariable().getName().toString(), TypeName.get(variableAndValue.getVariable().getType()));
        }

        classBuilder.addAnnotation(AnnotationSpec.builder(JsonTypeInfo.class)
                .addMember("include",
                        CodeBlock.builder()
                                .add("JsonTypeInfo.As.WRAPPER_OBJECT")
                                .build())
                .addMember("use",
                        CodeBlock.builder()
                                .add("JsonTypeInfo.Id.NAME")
                                .build())
                .build());

        final TypeSpec build = classBuilder.addMethod(constructorBuilder.build()).build();

        final JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, build).build();

        return javaFile;
    }

    /**
     * writes JavaFile using filer (generates POJO class in generated sources)
     * @param javaFile JavaFile representation of POJO
     */
    public void writeJavaFile(final JavaFile javaFile) {
        try {
            javaFile.writeTo(filer);
        } catch (Exception ignored) {
        }
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

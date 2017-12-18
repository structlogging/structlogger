package cz.muni.fi.processor.service;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.tree.JCTree;
import cz.muni.fi.LoggingEvent;
import cz.muni.fi.utils.VariableAndValue;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

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
     * @param name    name of POJO to be generated, if null, event is generated based on log literal (hash of it)
     * @param literal String literal used in structured log statement
     * @param usedVariables list of logging variables used by structured log statement
     * @return JavaFile representing Structured log Event (this JavaFile is not yet written, @see POJOService.writeJavaFile)
     */
    public JavaFile createPojo(final String name,
                               final JCTree.JCLiteral literal,
                               final List<VariableAndValue> usedVariables) {
        String eventName;
        if (name != null) {
            eventName = name;
        }
        else {
            eventName = "Event" + hash(literal.getValue().toString());
        }

        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(eventName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(TypeName.get(LoggingEvent.class));

        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

        addCommonLoggingEventFields(constructorBuilder);

        for (VariableAndValue variableAndValue : usedVariables) {
            addPojoField(classBuilder, constructorBuilder, variableAndValue.getVariable().getName().toString(), TypeName.get(variableAndValue.getVariable().getType()));
        }

        final TypeSpec build = classBuilder.addMethod(constructorBuilder.build()).build();

        final JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, build).build();

        return javaFile;
    }

    private void addCommonLoggingEventFields(final MethodSpec.Builder constructorBuilder) {
        constructorBuilder.addParameter(TypeName.get(String.class), "message", Modifier.FINAL);
        constructorBuilder.addParameter(TypeName.get(String.class), "sourceFile", Modifier.FINAL);
        constructorBuilder.addParameter(TypeName.LONG, "lineNumber", Modifier.FINAL);
        constructorBuilder.addParameter(TypeName.get(String.class), "type", Modifier.FINAL);
        constructorBuilder.addParameter(TypeName.LONG, "sid", Modifier.FINAL);
        constructorBuilder.addParameter(TypeName.get(String.class), "logLevel", Modifier.FINAL);
        constructorBuilder.addCode("super(message,sourceFile,lineNumber,type,sid,logLevel);");
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

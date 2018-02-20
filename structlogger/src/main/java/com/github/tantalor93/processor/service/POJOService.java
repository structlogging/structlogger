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
package com.github.tantalor93.processor.service;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.tree.JCTree;
import com.github.tantalor93.LoggingEvent;
import com.github.tantalor93.processor.exception.PackageNameException;
import com.github.tantalor93.processor.utils.VariableAndValue;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for creating POJO and generating them during annotation processing
 */
public class POJOService {

    private static final String PACKAGE_NAME = "structlogger.generated";

    private final Filer filer;
    private String generatedEventsPackage;
    private List<String> javaKeywords;

    public POJOService(final Filer filer, final String generatedEventsPackage) throws IOException, PackageNameException {
        this.filer = filer;

        this.generatedEventsPackage = StringUtils.isBlank(generatedEventsPackage) ? PACKAGE_NAME : generatedEventsPackage;
        try (InputStream resource = POJOService.class.getResourceAsStream("/file/javakeywords.txt")) {
            javaKeywords =
                    new BufferedReader(new InputStreamReader(resource,
                            StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        }

        //check each subpackage name
        for (String s : this.generatedEventsPackage.split("\\.")) {
            checkString(s);
        }
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
                               final List<VariableAndValue> usedVariables) throws PackageNameException {
        String eventName;
        String packageName;
        if (name != null) {
            //get event name and package name from qualified name
            final String[] split = name.split("\\.");
            eventName = split[split.length-1];
            checkString(eventName);
            final StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < split.length - 1; i++) {
                if (i != 0) {
                    stringBuffer.append(".");
                }
                stringBuffer.append(split[i]);
                checkString(split[i]);
            }
            packageName = stringBuffer.toString();

            //check that packageName does not contain java keyword
        }
        else {
            eventName = "Event" + hash(literal.getValue().toString());
            packageName = generatedEventsPackage;
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

        final JavaFile javaFile = JavaFile.builder(packageName, build).build();

        return javaFile;
    }

    private void checkString(final String s) throws PackageNameException {
        final boolean packageContainsJavaKeyword = javaKeywords.stream().anyMatch(e -> s.equals(e));
        if (packageContainsJavaKeyword || s.matches("\\d.*")) {
            throw new PackageNameException("string is not valid");
        }
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

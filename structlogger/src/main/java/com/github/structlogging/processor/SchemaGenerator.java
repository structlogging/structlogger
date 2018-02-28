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
package com.github.structlogging.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.github.structlogging.processor.utils.GeneratedClassInfo;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

/**
 * TaskListener, which takes care of generating json schemas for logging events, after GENERATE phase of compilation
 */
public class SchemaGenerator implements TaskListener {

    private static final String SCHEMA_04 = "http://json-schema.org/draft-04/schema#";
    private static final String JSON_SUFFIX = ".json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);

    private final Set<GeneratedClassInfo> eventsClassInfo;

    /**
     * absolute path, where folder with schemas should be created
     */
    private final String schemasRoot;

    public SchemaGenerator(final Set<GeneratedClassInfo> eventsClassInfo, final String schemasRoot) {
        this.eventsClassInfo = eventsClassInfo;
        this.schemasRoot = schemasRoot;
    }

    @Override
    public void started(final TaskEvent e) {

    }

    @Override
    public void finished(final TaskEvent e) {
        if (e.getKind() != TaskEvent.Kind.GENERATE) {
            return;
        }

        final Iterator<GeneratedClassInfo> iterator = eventsClassInfo.iterator();
        while (iterator.hasNext()) {
            final GeneratedClassInfo generatedGeneratedClassInfo = iterator.next();
            try {
                final Class<?> clazz = Class.forName(generatedGeneratedClassInfo.getQualifiedName());
                final JsonSchema schema = schemaGen.generateSchema(clazz);
                schema.set$schema(SCHEMA_04);
                schema.setDescription(generatedGeneratedClassInfo.getDescription());
                schema.asObjectSchema().setTitle(generatedGeneratedClassInfo.getQualifiedName());
                iterator.remove();
                createSchemaFile(generatedGeneratedClassInfo.getPackageName(), generatedGeneratedClassInfo.getSimpleName(), schema);
            } catch (Exception ex) {
                //IGNORE class is not accessible via reflection API
            }
        }
    }

    private void createSchemaFile(String namespace, String signature, JsonSchema schema) {
        try {
            final String dir = getDir(namespace);
            Files.createDirectories(Paths.get(dir));
            FileOutputStream out = new FileOutputStream(dir + signature + JSON_SUFFIX);
            out.write(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(schema));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDir(final String namespace) {
        final String pathWithoutNamespace = schemasRoot +
                File.separator +
                "schemas" +
                File.separator +
                "events" +
                File.separator;
        if (!StringUtils.isBlank(namespace)) {
            return pathWithoutNamespace +
                    namespace.replace(
                            ".",
                            File.separator
                    ) +
                    File.separator;
        }
        return pathWithoutNamespace;
    }
}

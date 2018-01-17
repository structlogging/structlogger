package cz.muni.fi.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import cz.muni.fi.utils.GeneratedClassInfo;

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
            } catch (Exception e1) {
                //TODO
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
        return schemasRoot + File.separator + "schemas" + File.separator + "events" + File.separator + namespace.replace(".", File.separator) + File.separator;
    }
}

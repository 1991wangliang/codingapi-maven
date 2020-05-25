package com.codingapi.maven.uml.builder;

import com.codingapi.maven.uml.define.ModelDefinition;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class UmlBuilder implements IBuilder, Closeable {

    private final FileWriter fileWriter;

    public UmlBuilder(Path filePath) throws IOException {
        fileWriter = new FileWriter(filePath.toFile());
        try {
            fileWriter.write("@startuml\n");
            fileWriter.write("set namespaceSeparator ::\n\n");
            fileWriter.write("skinparam linetype polyline\n");
            fileWriter.write("skinparam linetype polyline\n");
            fileWriter.write("skinparam linetype ortho\n");
            fileWriter.write("top to bottom direction\n\n");
        } catch (IOException e) {
            fileWriter.close();
            throw e;
        }
    }

    @SneakyThrows
    @Override
    public void appendModel(ModelDefinition modelDefinition) {
        fileWriter.write(String.format("class %s::%s <<(%s,%s)%s>> {\n",
                modelDefinition.getPackageName(),
                modelDefinition.getClassName(),
                modelDefinition.getAnnotation().getFlag(),
                modelDefinition.getAnnotation().getColor(), modelDefinition.getAnnotation().getTitle()));

        modelDefinition.getFieldDefinitions().forEach(fieldDefinition -> {
            try {
                fileWriter.write(String.format("\t-%s:%s %s\n",
                        fieldDefinition.getName(),
                        fieldDefinition.getType(),
                        StringUtils.isNotEmpty(fieldDefinition.getRemark()) ? "<" + fieldDefinition.getRemark() + ">" : ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        fileWriter.write("\n");

        modelDefinition.getMethodDefinitions().forEach(methodDefinition -> {
            try {
                fileWriter.write(String.format("\t+%s(%s): %s %s\n",
                        methodDefinition.getName(),
                        methodDefinition.getParameterTypes(),
                        methodDefinition.getReturnType(), StringUtils.isNotEmpty(methodDefinition.getRemark()) ? "<" + methodDefinition.getRemark() + ">" : ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fileWriter.write("}\n\n");

        modelDefinition.getRelationDefinitions().forEach(relationDefinition -> {
            try {
                fileWriter.write(String.format("%s %s %s\n", relationDefinition.getRelLeft(), relationDefinition.getRel(), relationDefinition.getRelRight()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        fileWriter.write("\n");

    }

    @Override
    public void close() throws IOException {
        if (fileWriter != null) {
            try {
                fileWriter.write("\n@enduml\n");
                fileWriter.close();
            } catch (IOException e) {
                fileWriter.close();
            }
        }
    }
}

package com.codingapi.maven.uml.builder;

import com.codingapi.maven.uml.define.ModelDefinition;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

public class UmlPlantUMLWriter extends IPlantUMLWriter {

    public UmlPlantUMLWriter(Path filePath) throws IOException {
       super(filePath);
    }


    @SneakyThrows
    @Override
    public void content(ModelDefinition modelDefinition) {
        fileWriter(String.format("class %s::%s <<(%s,%s)%s>> {\n",
                modelDefinition.getPackageName(),
                modelDefinition.getClassName(),
                modelDefinition.getAnnotation().getFlag(),
                modelDefinition.getAnnotation().getColor(),
                modelDefinition.getAnnotation().getValue()));

        modelDefinition.getFieldDefinitions().forEach(fieldDefinition -> {
            try {
                fileWriter(String.format("\t%s%s:%s %s\n",
                        fieldDefinition.getAccessTypeFlag(),
                        fieldDefinition.getName(),
                        fieldDefinition.getType(),
                        StringUtils.isNotEmpty(fieldDefinition.getRemark()) ? "<" + fieldDefinition.getRemark() + ">" : ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        fileWriter("\n");

        modelDefinition.getMethodDefinitions().forEach(methodDefinition -> {
            try {
                fileWriter(String.format("\t%s%s(%s): %s %s\n",
                        methodDefinition.getAccessTypeFlag(),
                        methodDefinition.getName(),
                        methodDefinition.getParameterType(),
                        methodDefinition.getReturnType(),
                        StringUtils.isNotEmpty(methodDefinition.getRemark()) ? "<" + methodDefinition.getRemark() + ">" : ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fileWriter("}\n\n");

        modelDefinition.getRelationDefinitions().forEach(relationDefinition -> {
            try {
                fileWriter(String.format("%s %s %s\n",
                        relationDefinition.getRelLeft(),
                        relationDefinition.getRel(),
                        relationDefinition.getRelRight()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        fileWriter("\n");
    }


}

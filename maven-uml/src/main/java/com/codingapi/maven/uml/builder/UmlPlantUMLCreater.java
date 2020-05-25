package com.codingapi.maven.uml.builder;

import com.codingapi.maven.uml.define.ModelDefinition;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

public class UmlPlantUMLCreater extends IPlantUMLCreater {

    public UmlPlantUMLCreater(Path filePath) throws IOException {
       super(filePath);
    }


    @SneakyThrows
    @Override
    public void appendModel(ModelDefinition modelDefinition) {
        write(String.format("class %s::%s <<(%s,%s)%s>> {\n",
                modelDefinition.getPackageName(),
                modelDefinition.getClassName(),
                modelDefinition.getAnnotation().getFlag(),
                modelDefinition.getAnnotation().getColor(), modelDefinition.getAnnotation().getTitle()));

        modelDefinition.getFieldDefinitions().forEach(fieldDefinition -> {
            try {
                write(String.format("\t-%s:%s %s\n",
                        fieldDefinition.getName(),
                        fieldDefinition.getType(),
                        StringUtils.isNotEmpty(fieldDefinition.getRemark()) ? "<" + fieldDefinition.getRemark() + ">" : ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        write("\n");

        modelDefinition.getMethodDefinitions().forEach(methodDefinition -> {
            try {
                write(String.format("\t+%s(%s): %s %s\n",
                        methodDefinition.getName(),
                        methodDefinition.getParameterType(),
                        methodDefinition.getReturnType(), StringUtils.isNotEmpty(methodDefinition.getRemark()) ? "<" + methodDefinition.getRemark() + ">" : ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        write("}\n\n");

        modelDefinition.getRelationDefinitions().forEach(relationDefinition -> {
            try {
                write(String.format("%s %s %s\n", relationDefinition.getRelLeft(), relationDefinition.getRel(), relationDefinition.getRelRight()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        write("\n");
    }


}

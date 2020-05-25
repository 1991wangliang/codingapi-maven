package com.codingapi.maven.uml.builder;


import com.codingapi.maven.uml.define.ModelDefinition;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public abstract class IPlantUMLCreater implements Closeable {

    abstract void appendModel(ModelDefinition modelDefinition) throws IOException;


    private final FileWriter fileWriter;

    public IPlantUMLCreater(Path filePath) throws IOException {
        fileWriter = new FileWriter(filePath.toFile());
        this.header();
    }

    protected void write(String content)throws IOException{
        fileWriter.write(content);
    }

    private void header()throws IOException{
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

package com.codingapi.maven.uml.builder;


import com.codingapi.maven.uml.define.ModelDefinition;
import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public abstract class IPlantUMLWriter implements Closeable {

    abstract void content(ModelDefinition modelDefinition) throws IOException;
    private final FileWriter fileWriter;

    public IPlantUMLWriter(Path filePath) throws IOException {
        fileWriter = new FileWriter(filePath.toFile());
    }

    @SneakyThrows
    public void write(ModelDefinition modelDefinition){
        this.header();
        content(modelDefinition);
        this.footer();
    }

    protected void fileWriter(String content)throws IOException{
        fileWriter.write(content);
    }

    private void header() throws IOException{
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

    private void footer() throws IOException{
        try {
            fileWriter.write("\n@enduml\n");
        } catch (IOException e) {
            fileWriter.close();
            throw e;
        }
    }

    @Override
    public void close(){
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
        }
    }


}

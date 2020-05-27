package com.codingapi.maven.uml.define;


import com.codingapi.maven.uml.annotation.AggregationRootModel;
import com.codingapi.maven.uml.annotation.Ignore;
import com.codingapi.maven.uml.annotation.Model;
import com.codingapi.maven.uml.builder.UmlPlantUMLWriter;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.SneakyThrows;

import java.nio.file.Paths;
import java.util.stream.Collectors;

@AggregationRootModel(value = "生成PlantUML工具")
public class PlantUmlBuilder {

    private final String generatePath;
    private final String basePackage;
    private final ClassLoader classLoader;


    public PlantUmlBuilder(String generatePath, String basePackage, ClassLoader classLoader) {
        this.generatePath = generatePath;
        this.basePackage = basePackage;
        this.classLoader = classLoader;
    }

    @SneakyThrows
    public void run() {
        try (UmlPlantUMLWriter umlWriter = new UmlPlantUMLWriter(Paths.get(generatePath))) {
            umlWriter.header();
            ClassGraph classGraph = new ClassGraph()
                    .addClassLoader(classLoader)
                    .verbose()
                    .enableAllInfo()
                    .whitelistPackages(basePackage);
            classGraph
                    .scan()
                    .getAllClasses()
                    .stream()
                    .filter(classInfo -> classInfo.getAnnotationInfo(Ignore.class.getName()) == null)
                    .filter(classInfo -> classInfo.getAnnotationInfo(Model.class.getName()) != null)
                    .filter(classInfo -> !classInfo.isAnnotation())
                    .map(classInfo -> {
                        return this.classInfoParser(classGraph.scan(),classInfo);
                    }).collect(Collectors.toList())
                    .forEach(umlWriter::write);
            umlWriter.footer();
        }
    }


    private ModelDefinition classInfoParser(ScanResult scanResult, ClassInfo classInfo) {
        ModelDefinitionParser modelDefinitionParser = new ModelDefinitionParser(scanResult,classInfo);
        return modelDefinitionParser.parser();
    }


}

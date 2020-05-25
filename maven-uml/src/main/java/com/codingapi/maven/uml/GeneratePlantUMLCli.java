package com.codingapi.maven.uml;


import com.codingapi.maven.uml.annotation.*;
import com.codingapi.maven.uml.builder.UmlBuilder;
import com.codingapi.maven.uml.define.*;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.PackageInfo;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AggregationRootModel(title = "生成PlantUML工具")
public class GeneratePlantUMLCli implements Cli {

    private final String generatePath;
    private final String basePackage;
    private final ClassLoader classLoader;


    public GeneratePlantUMLCli(String generatePath, String basePackage,ClassLoader classLoader) {
        this.generatePath = generatePath;
        this.basePackage = basePackage;
        this.classLoader = classLoader;
    }

    @Override
    public void run() throws Exception {
        try (UmlBuilder umlBuilder = new UmlBuilder(Paths.get(generatePath))) {
            new ClassGraph()
                    .addClassLoader(classLoader)
                    .verbose()
                    .enableAllInfo()
                    .whitelistPackages(basePackage)
                    .scan()
                    .getAllClasses()
                    .stream()
                    .filter(classInfo -> classInfo.getAnnotationInfo(Model.class.getName()) != null)
                    .filter(classInfo -> !classInfo.isAnnotation())
                    .map(this::toModelDefinition).collect(Collectors.toList()).forEach(umlBuilder::appendModel);
        }
    }


    private ModelDefinition toModelDefinition(ClassInfo classInfo) {

        // Annotation Info
        AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(Model.class.getName());
        ModelAnnotation modelAnnotation = new ModelAnnotation();
        modelAnnotation.setFlag((String) annotationInfo.getParameterValues().getValue("flag"));
        modelAnnotation.setColor((String) annotationInfo.getParameterValues().getValue("color"));
        modelAnnotation.setTitle((String) annotationInfo.getParameterValues().getValue("title"));
        if (StringUtils.isEmpty(modelAnnotation.getTitle())) {
            AnnotationInfo tmp = classInfo.getAnnotationInfo()
                    .stream()
                    .filter(ai -> !Objects.nonNull(ai.getParameterValues().getValue("title")))
                    .findAny()
                    .orElse(null);
            if (Objects.nonNull(tmp)) {
                modelAnnotation.setTitle((String) tmp.getParameterValues().getValue("title"));
            } else {
                modelAnnotation.setTitle("");
            }
        }

        // Package
        String packageName = chooseBoundContext(classInfo.getPackageInfo()).orElse(classInfo.getPackageName());

        // Fields
        List<FieldDefinition> fieldDefinitions = new LinkedList<>();
        classInfo.getDeclaredFieldInfo().forEach(fieldInfo -> {
            FieldDefinition fieldDefinition = new FieldDefinition();
            fieldDefinition.setName(fieldInfo.getName());
            fieldDefinition.setType(fieldInfo.getTypeDescriptor().toStringWithSimpleNames());

            AnnotationInfo remark = fieldInfo.getAnnotationInfo(Title.class.getName());
            if (Objects.nonNull(remark)) {
                fieldDefinition.setRemark((String) remark.getParameterValues().getValue("value"));
            }
            fieldDefinitions.add(fieldDefinition);
        });

        // Methods
        List<MethodDefinition> methodDefinitions = new LinkedList<>();

        classInfo.getDeclaredMethodInfo().stream()
                .filter(methodInfo -> !methodInfo.getName().contains("hashCode"))
                .filter(methodInfo -> !methodInfo.getName().contains("toString"))
                .filter(methodInfo -> !methodInfo.getName().equals("equals"))
                .filter(methodInfo -> !methodInfo.getName().equals("canEqual"))
                .filter(methodInfo -> methodInfo.getModifiers() == Modifier.PUBLIC)
                .forEach(methodInfo -> {
                    MethodDefinition methodDefinition = new MethodDefinition();
                    methodDefinition.setName(methodInfo.getName());
                    methodDefinition.setReturnType(
                            methodInfo.getTypeSignatureOrTypeDescriptor().getResultType().toStringWithSimpleNames());

                    methodDefinition.setParameterTypes(
                            Stream.of(methodInfo.getParameterInfo())
                                    .map(pi -> pi.getTypeSignatureOrTypeDescriptor().toStringWithSimpleNames())
                                    .collect(Collectors.joining(", ")));

                    AnnotationInfo remark = methodInfo.getAnnotationInfo(Title.class.getName());
                    if (Objects.nonNull(remark)) {
                        methodDefinition.setRemark((String) remark.getParameterValues().getValue("value"));
                    }
                    methodDefinitions.add(methodDefinition);
                });

        // Relations
        Set<RelationDefinition> relationDefinitionSet = new HashSet<>(getFromFields(classInfo, packageName));
        relationDefinitionSet.addAll(getFromMethods(classInfo));
        relationDefinitionSet.addAll(getFromConstructor(classInfo));


        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setAnnotation(modelAnnotation);
        modelDefinition.setPackageName(Optional.ofNullable(packageName).orElse(classInfo.getPackageName()));
        modelDefinition.setClassName(classInfo.getSimpleName());

        modelDefinition.setFieldDefinitions(fieldDefinitions);
        modelDefinition.setMethodDefinitions(methodDefinitions);
        modelDefinition.setRelationDefinitions(new LinkedList<>(relationDefinitionSet));
        return modelDefinition;
    }

    private Optional<String> chooseBoundContext(PackageInfo packageInfo) {
        if (packageInfo.getAnnotationInfo(BoundContext.class.getName()) != null) {
            String packageName =
                    (String) packageInfo.getAnnotationInfo(BoundContext.class.getName()).getParameterValues().getValue("value");

            return Optional.of(StringUtils.isNotEmpty(packageName) ? packageName : packageInfo.getName());
        }
        if (Objects.isNull(packageInfo.getParent())) {
            return Optional.empty();
        }
        return chooseBoundContext(packageInfo.getParent());
    }

    private Optional<String> chooseBoundContext(Package pk) {
        if (pk.getAnnotation(BoundContext.class) != null) {
            String packageName = pk.getAnnotation(BoundContext.class).value();
            return Optional.of(StringUtils.isNotEmpty(packageName) ? packageName : pk.getName());
        }

        int idx = pk.getName().lastIndexOf('.');

        Package parent = Package.getPackage(pk.getName().substring(0, idx));
        if (Objects.isNull(parent)) {
            return Optional.empty();
        }
        return chooseBoundContext(parent);
    }


    private Set<RelationDefinition> getFromFields(ClassInfo classInfo, String packageName) {
        Set<RelationDefinition> relationDefinitionSet = new HashSet<>();
        classInfo.getDeclaredFieldInfo().forEach(fieldInfo -> {
            Field field = fieldInfo.loadClassAndGetField();
            GraphRelation graphPosition = field.getAnnotatedType().getAnnotation(GraphRelation.class);

            if (Objects.nonNull(graphPosition)) {
                String boundCtx =
                        chooseBoundContext(field.getType().getPackage()).orElse(field.getType().getPackage().getName());
                relationDefinitionSet.add(RelationDefinition.of(
                        packageName + "::" + classInfo.getSimpleName(), graphPosition.value(),
                        boundCtx + "::" + field.getType().getSimpleName()));
                return;
            }
            if (AnnotatedParameterizedType.class.isAssignableFrom(field.getAnnotatedType().getClass())) {
                AnnotatedType annotatedType =
                        ((AnnotatedParameterizedType) field.getAnnotatedType()).getAnnotatedActualTypeArguments()[0];
                GraphRelation graphPosition1 = annotatedType.getAnnotation(GraphRelation.class);
                if (Objects.nonNull(graphPosition1)) {
                    int idx = annotatedType.getType().getTypeName().lastIndexOf('.');
                    String classSimpleName = annotatedType.getType().getTypeName().substring(idx + 1);
                    String pkName = annotatedType.getType().getTypeName().substring(0, idx);
                    String boundCtx =
                            Optional.ofNullable(Optional.ofNullable(packageName).orElse(classInfo.getPackageName())).orElse(pkName);
                    relationDefinitionSet.add(RelationDefinition.of(
                            packageName + "::" + classInfo.getSimpleName(), graphPosition1.value(),
                            boundCtx + "::" + classSimpleName));
                }
            }
        });
        return relationDefinitionSet;
    }

    private Set<RelationDefinition> getFromMethods(ClassInfo classInfo) {
        Set<RelationDefinition> relationDefinitionSet = new HashSet<>();

        return relationDefinitionSet;
    }

    private Set<RelationDefinition> getFromConstructor(ClassInfo classInfo) {

        Set<RelationDefinition> relationDefinitionSet = new HashSet<>();

        return relationDefinitionSet;
    }

}

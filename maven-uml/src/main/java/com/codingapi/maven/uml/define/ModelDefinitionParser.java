package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.*;
import io.github.classgraph.AnnotationClassRef;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.PackageInfo;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelDefinitionParser {

    private ClassInfo classInfo;
    private ModelDefinition modelDefinition;


    public ModelDefinitionParser(ClassInfo classInfo) {
        this.classInfo = classInfo;
        this.modelDefinition = new ModelDefinition();

    }

    private ModelAnnotation modelAnnotation() {
        // Annotation Info
        AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(Model.class.getName());
        ModelAnnotation modelAnnotation = new ModelAnnotation();
        modelAnnotation.setFlag(getStringValue(annotationInfo,UmlConstant.FLAG));
        modelAnnotation.setColor(getStringValue(annotationInfo,UmlConstant.COLOR));
        modelAnnotation.setValue(getStringValue(annotationInfo,UmlConstant.VALUE));

        if (StringUtils.isEmpty(modelAnnotation.getValue())) {
            AnnotationInfo tmp = classInfo.getAnnotationInfo()
                    .stream()
                    .filter(ai -> StringUtils.isNotEmpty(getStringValue(ai, UmlConstant.VALUE)))
                    .findAny()
                    .orElse(null);
            if (Objects.nonNull(tmp)) {
                modelAnnotation.setValue(getStringValue(tmp, UmlConstant.VALUE));
            } else {
                modelAnnotation.setValue("");
            }
        }
        return modelAnnotation;
    }

    private List<FieldDefinition> fieldDefinitionList() {
        // Fields
        List<FieldDefinition> fieldDefinitions = new LinkedList<>();
        classInfo.getDeclaredFieldInfo()
                .filter(fieldInfo -> fieldInfo.getAnnotationInfo(Ignore.class.getName())==null)
                .forEach(fieldInfo -> {
            FieldDefinition fieldDefinition = new FieldDefinition();
            fieldDefinition.setName(fieldInfo.getName());
            fieldDefinition.setType(fieldInfo.getTypeSignatureOrTypeDescriptor().toStringWithSimpleNames());

            AnnotationInfo remark = fieldInfo.getAnnotationInfo(Title.class.getName());
            if (Objects.nonNull(remark)) {
                fieldDefinition.setRemark(getStringValue(remark, UmlConstant.VALUE));
            }
            fieldDefinitions.add(fieldDefinition);
        });
        return fieldDefinitions;
    }

    private List<MethodDefinition> methodDefinitionList() {

        // Methods
        List<MethodDefinition> methodDefinitions = new LinkedList<>();

        classInfo.getDeclaredMethodInfo().stream()
                .filter(methodInfo -> methodInfo.getAnnotationInfo(Ignore.class.getName())==null)
                .filter(methodInfo -> methodInfo.getModifiers() == Modifier.PUBLIC)
                .forEach(methodInfo -> {
                    MethodDefinition methodDefinition = new MethodDefinition();
                    methodDefinition.setName(methodInfo.getName());
                    methodDefinition.setReturnType(
                            methodInfo.getTypeSignatureOrTypeDescriptor().getResultType().toStringWithSimpleNames());

                    methodDefinition.setParameterType(
                            Stream.of(methodInfo.getParameterInfo())
                                    .map(pi -> pi.getTypeSignatureOrTypeDescriptor().toStringWithSimpleNames())
                                    .collect(Collectors.joining(", ")));

                    AnnotationInfo remark = methodInfo.getAnnotationInfo(Title.class.getName());
                    if (Objects.nonNull(remark)) {
                        methodDefinition.setRemark(getStringValue(remark, UmlConstant.VALUE));
                    }
                    methodDefinitions.add(methodDefinition);
                });
        return methodDefinitions;
    }


    private String getStringValue(AnnotationInfo annotationInfo, String key) {
        return (String) annotationInfo.getParameterValues().getValue(key);
    }

    private Class<?> getClassValue(AnnotationInfo annotationInfo, String key) {
        AnnotationClassRef annotationClassRef = (AnnotationClassRef) annotationInfo.getParameterValues().getValue(key);
        return annotationClassRef.loadClass();
    }

    private Optional<String> chooseBoundContext(PackageInfo packageInfo) {
        AnnotationInfo annotationInfo = packageInfo.getAnnotationInfo(BoundContext.class.getName());

        if (annotationInfo != null) {
            String packageName = getStringValue(annotationInfo, UmlConstant.VALUE);
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
        System.out.println(pk.getName()+":"+pk.getName().substring(0, idx));
        Package parent = Package.getPackage(pk.getName().substring(0, idx));
        if (Objects.isNull(parent)) {
            return Optional.empty();
        }
        return chooseBoundContext(parent);
    }



    private List<RelationDefinition> relationDefinitions(String packageName) {
        List<RelationDefinition> relationDefinitionList = new ArrayList<>();

        Consumer<AnnotationInfo> consumer = annotationInfo -> {
            Class<?> typeClass = getClassValue(annotationInfo,UmlConstant.TYPE);
            String boundCtx =
                    chooseBoundContext(typeClass.getPackage()).orElse(typeClass.getPackage().getName());
            relationDefinitionList.add(RelationDefinition.of(
                    packageName + "::" + classInfo.getSimpleName(), getStringValue(annotationInfo,UmlConstant.VALUE),
                    boundCtx + "::" + typeClass.getSimpleName()));
        };

        classInfo.getDeclaredFieldInfo()
                .filter(fieldInfo -> fieldInfo.getAnnotationInfo(GraphRelation.class.getName())!=null)
                .forEach(fieldInfo -> {
            AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(GraphRelation.class.getName());
            consumer.accept(annotationInfo);
        });

        classInfo.getDeclaredMethodInfo()
                .filter(fieldInfo -> fieldInfo.getAnnotationInfo(GraphRelation.class.getName())!=null)
                .forEach(fieldInfo -> {
                    AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(GraphRelation.class.getName());
                    consumer.accept(annotationInfo);
                });

        return relationDefinitionList;
    }


    @GraphRelation(value = ".left.>",type = ModelDefinition.class)
    public ModelDefinition parser() {
        // Package
        String packageName = chooseBoundContext(classInfo.getPackageInfo()).orElse(classInfo.getPackageName());
        modelDefinition.setPackageName(Optional.ofNullable(packageName).orElse(classInfo.getPackageName()));
        modelDefinition.setClassName(classInfo.getSimpleName());
        modelDefinition.setAnnotation(modelAnnotation());
        modelDefinition.setFieldDefinitions(fieldDefinitionList());
        modelDefinition.setMethodDefinitions(methodDefinitionList());
        modelDefinition.setRelationDefinitions(relationDefinitions(packageName));
        return modelDefinition;
    }
}

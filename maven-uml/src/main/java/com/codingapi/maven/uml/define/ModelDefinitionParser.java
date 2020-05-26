package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.*;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.PackageInfo;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelDefinitionParser {

    private ClassInfo classInfo;
    private ModelDefinition modelDefinition;
    private AnnotationInfo annotationInfo;

    public ModelDefinitionParser(ClassInfo classInfo) {
        this.classInfo = classInfo;
        this.modelDefinition = new ModelDefinition();
        // Annotation Info
        annotationInfo = classInfo.getAnnotationInfo(Model.class.getName());
    }

    private ModelAnnotation modelAnnotation() {
        ModelAnnotation modelAnnotation = new ModelAnnotation();
        modelAnnotation.setFlag(getStringValue(UmlConstant.FLAG));
        modelAnnotation.setColor(getStringValue(UmlConstant.COLOR));
        modelAnnotation.setTitle(getStringValue(UmlConstant.TITLE));
        if (StringUtils.isEmpty(modelAnnotation.getTitle())) {
            AnnotationInfo tmp = classInfo.getAnnotationInfo()
                    .stream()
                    .filter(ai -> StringUtils.isNotEmpty(getStringValue(ai, UmlConstant.TITLE)))
                    .findAny()
                    .orElse(null);
            if (Objects.nonNull(tmp)) {
                modelAnnotation.setTitle(getStringValue(tmp, UmlConstant.TITLE));
            } else {
                modelAnnotation.setTitle("");
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

    private List<RelationDefinition> relationDefinitions(String packageName) {
        // Relations
        List<RelationDefinition> relationDefinitionSet = new ArrayList<>(getFromFields(classInfo, packageName));
        relationDefinitionSet.addAll(getFromMethods(classInfo));
        relationDefinitionSet.addAll(getFromConstructor(classInfo));
        return relationDefinitionSet;
    }

    private String getStringValue(String key) {
        return getStringValue(annotationInfo, key);
    }

    private String getStringValue(AnnotationInfo annotationInfo, String key) {
        return (String) annotationInfo.getParameterValues().getValue(key);
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

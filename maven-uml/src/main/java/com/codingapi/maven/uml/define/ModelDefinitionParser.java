package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.*;
import io.github.classgraph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelDefinitionParser {

    private ClassInfo classInfo;
    private ModelDefinition modelDefinition;
    private ScanResult scanResult;
    private String filterMethod;

    public ModelDefinitionParser(ScanResult scanResult, ClassInfo classInfo,String filterMethod) {
        this.scanResult = scanResult;
        this.classInfo = classInfo;
        this.filterMethod = filterMethod;
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
            fieldDefinition.setAccessType(fieldInfo.getModifierStr());
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
                .filter(methodInfo -> !methodInfo.getName().startsWith("lambda"))
                .filter(methodInfo -> !filterMethod.contains(methodInfo.getName()))
                .filter(methodInfo -> !modelDefinition.containsField(methodInfo.getName()))
                .forEach(methodInfo -> {
                    MethodDefinition methodDefinition = new MethodDefinition();
                    methodDefinition.setAccessType(methodInfo.getModifiersStr());
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



    private List<RelationDefinition> relationDefinitions(String packageName) {
        List<RelationDefinition> relationDefinitionList = new ArrayList<>();
        Consumer<AnnotationInfo> consumer = annotationInfo -> {
            Class<?> typeClass = getClassValue(annotationInfo,UmlConstant.TYPE);
            PackageInfo packageInfo =  scanResult.getPackageInfo(typeClass.getPackage().getName());
            String boundCtx =
                    chooseBoundContext(packageInfo).orElse(typeClass.getPackage().getName());
            relationDefinitionList.add(RelationDefinition.of(
                    packageName + "::" + classInfo.getSimpleName(), getStringValue(annotationInfo,UmlConstant.VALUE),
                    boundCtx + "::" + typeClass.getSimpleName()));
        };

        //对象关系: 继承、实现
        classInfo.getAnnotationInfo()
                .filter(annotationInfo -> annotationInfo.getName().equals(GraphRelation.class.getName()))
                .forEach(consumer::accept);

        classInfo.getAnnotationInfo()
                .filter(annotationInfo -> annotationInfo.getName().equals(GraphRelations.class.getName()))
                .forEach(annotationInfo -> {
                    Object[] graphRelations = (Object[])annotationInfo.getParameterValues().getValue(UmlConstant.VALUE);
                    for (Object relation:graphRelations){
                        AnnotationInfo relationInfo = (AnnotationInfo)relation;
                        consumer.accept(relationInfo);
                    }
                });

        //参数的关系:关联、聚合、组合
        classInfo.getDeclaredFieldInfo()
                .filter(methodInfo -> methodInfo.getAnnotationInfo(GraphRelations.class.getName())!=null)
                .forEach(methodInfo -> {
                    AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(GraphRelations.class.getName());
                    Object[] graphRelations = (Object[])annotationInfo.getParameterValues().getValue(UmlConstant.VALUE);
                    for (Object relation:graphRelations){
                        AnnotationInfo relationInfo = (AnnotationInfo)relation;
                        consumer.accept(relationInfo);
                    }
                });

        classInfo.getDeclaredFieldInfo()
                .filter(fieldInfo -> fieldInfo.getAnnotationInfo(GraphRelation.class.getName())!=null)
                .forEach(fieldInfo -> {
            AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(GraphRelation.class.getName());
            consumer.accept(annotationInfo);
        });

        //方法关系:依赖
        classInfo.getDeclaredMethodInfo()
                .filter(fieldInfo -> fieldInfo.getAnnotationInfo(GraphRelations.class.getName())!=null)
                .forEach(fieldInfo -> {
                    AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(GraphRelations.class.getName());
                    Object[] graphRelations = (Object[])annotationInfo.getParameterValues().getValue(UmlConstant.VALUE);
                    for (Object relation:graphRelations){
                        AnnotationInfo relationInfo = (AnnotationInfo)relation;
                        consumer.accept(relationInfo);
                    }
                });

        classInfo.getDeclaredMethodInfo()
                .filter(methodInfo -> methodInfo.getAnnotationInfo(GraphRelation.class.getName())!=null)
                .forEach(methodInfo -> {
                    AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(GraphRelation.class.getName());
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

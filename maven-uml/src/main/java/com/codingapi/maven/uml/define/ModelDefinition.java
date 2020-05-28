package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.AggregationRootModel;
import com.codingapi.maven.uml.annotation.GraphRelation;
import com.codingapi.maven.uml.annotation.Title;
import lombok.Data;

import java.util.List;

@AggregationRootModel(value = "模型定义")
@Data
public class ModelDefinition {
    private String packageName;

    private String className;

    @GraphRelation(value = ".right.>",type = ModelAnnotation.class)
    @Title("模型注释")
    private ModelAnnotation annotation;

    @GraphRelation(value = ".left.>",type = FieldDefinition.class)
    private List<FieldDefinition> fieldDefinitions;


    private String methodNameUpperCase(String methodName){
        String method = methodName.toUpperCase();
        if(method.startsWith("SET")) {
            return  method.replaceFirst("SET","");
        }
        if(method.startsWith("GET")) {
            return  method.replaceFirst("GET","");
        }
        if(method.startsWith("IS")) {
            return  method.replaceFirst("IS","");
        }
        return method;
    }


    public boolean containsField(String methodName){
        String method =methodNameUpperCase(methodName);
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            if(fieldDefinition.getName().toUpperCase().equals(method)){
                return true;
            }
        }
        return false;
    }


    @GraphRelation(value = ".down.>",type = MethodDefinition.class)
    private List<MethodDefinition> methodDefinitions;

    @GraphRelation(value = ".left.>",type = RelationDefinition.class)
    private List<RelationDefinition> relationDefinitions;

    @Title("模型转字符串")
    public String string() {
        return toString();
    }

}

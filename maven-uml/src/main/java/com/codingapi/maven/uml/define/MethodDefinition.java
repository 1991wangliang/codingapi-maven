package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.ValueObjectModel;
import lombok.Data;

@ValueObjectModel(title = "方法定义")
@Data
public class MethodDefinition {
    private String name;
    private String returnType;
    private String parameterTypes;
    private String remark = "";
}

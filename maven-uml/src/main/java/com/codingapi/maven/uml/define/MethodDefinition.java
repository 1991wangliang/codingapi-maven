package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.ValueObjectModel;
import lombok.Data;

@ValueObjectModel(value = "方法定义")
@Data
public class MethodDefinition extends IAccessTypeFlag{

    private String name;
    private String returnType;
    private String parameterType;
    private String remark = "";

}

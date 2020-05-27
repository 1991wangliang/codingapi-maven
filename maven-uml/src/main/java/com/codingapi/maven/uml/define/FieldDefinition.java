package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.ValueObjectModel;
import lombok.Data;

@ValueObjectModel(value = "字段定义")
@Data
public class FieldDefinition {
    private String name;
    private String type;
    private String remark = "";
}

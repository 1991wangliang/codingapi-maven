package com.codingapi.maven.uml.define;

import com.codingapi.maven.uml.annotation.ValueObjectModel;
import lombok.Getter;

@ValueObjectModel(title = "模型注释")
@Getter
public class ModelAnnotation {
    private String flag;
    private String color;
    private String title;

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

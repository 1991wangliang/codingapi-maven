package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Model(flag = "V", color = "#FFEE00", title = "")
public @interface ValueObjectModel {
    String title();
}

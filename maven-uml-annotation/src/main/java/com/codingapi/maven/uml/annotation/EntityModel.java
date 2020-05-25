package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
@Model(flag = "E", color = "#FFEE00", title = "")
public @interface EntityModel {
    String title();
}

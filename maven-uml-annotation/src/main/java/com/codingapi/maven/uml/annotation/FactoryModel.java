package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Model(flag = "F", color = "#FFEE00", value = "")
public @interface FactoryModel {
    String value();
}

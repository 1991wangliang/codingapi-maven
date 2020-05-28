package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Model(flag = "S", color = "#FFEE00", value = "")
public @interface ServiceModel {
    String value();
}

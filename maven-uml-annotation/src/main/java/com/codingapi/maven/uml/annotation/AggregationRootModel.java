package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Model(flag = "A", color = "#FFEE00", value = "")
public @interface AggregationRootModel {
    String value();
}

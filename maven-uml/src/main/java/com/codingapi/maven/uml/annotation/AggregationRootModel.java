package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
@Model(flag = "A", color = "#FFEE00", title = "")
public @interface AggregationRootModel {
    String title();
}

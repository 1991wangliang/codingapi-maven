package com.codingapi.maven.uml.annotation;

import java.lang.annotation.*;

/**
 * 描述 字段 方法 类型 的含义
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Title {
    /**
     * 描述
     *
     * @return not null string
     */
    String value();
}

package com.codingapi.maven.cole;

import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.executor.Executor;

public enum MarkdownType {

    EXECUTOR,EVENT;


    public static MarkdownType parser(Class<?> clazz){
        if(clazz.getAnnotation(Executor.class)!=null){
            return EXECUTOR;
        }
        if(clazz.getAnnotation(EventHandler.class)!=null){
            return EVENT;
        }
        return EXECUTOR;
    }
}

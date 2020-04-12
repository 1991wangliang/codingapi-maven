package com.codingapi.maven.cole;

import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.executor.Executor;

public enum MarkdownType {

    EXECUTOR,EVENT;


    public static MarkdownType parser(Class<?> clazz){
        if(clazz.equals(Executor.class)){
            return EXECUTOR;
        }
        if(clazz.equals(EventHandler.class)){
            return EVENT;
        }
        return EXECUTOR;
    }
}

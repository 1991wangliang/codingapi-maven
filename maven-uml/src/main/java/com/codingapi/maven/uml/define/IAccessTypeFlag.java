package com.codingapi.maven.uml.define;

import lombok.Data;

/**
 * @author lorne
 * @date 2020/5/28
 * @description
 */
@Data
public abstract class IAccessTypeFlag {

    private String accessType;

    public String getAccessTypeFlag(){
        if(accessType.equals("public")){
            return "+";
        }
        if(accessType.equals("private")){
            return "-";
        }
        if(accessType.equals("protected")){
            return "#";
        }
        return "+";
    }
}

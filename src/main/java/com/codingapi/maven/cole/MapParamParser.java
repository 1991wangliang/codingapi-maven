package com.codingapi.maven.cole;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class MapParamParser {

    private MapParam mapParam;

    public MapParamParser() {
        mapParam = new MapParam();
    }

    public MapParamParser(MapParam mapParam) {
        this.mapParam = mapParam;
    }

    public MapParamParser parser(String str) {
        str = str.replaceFirst("\\{","");
        str = str.replaceFirst("\\}","");
        str = str.replaceFirst("@","");
        String[] kvs = str.split(":");
        String key = kvs[0];
        String valueString =kvs[1];
        put(key,valueString);
        return this;
    }

    @SneakyThrows
    private void put(String key, String val) {
        if(val!=null&&!"".equals(val)){
            Field field =  MapParam.class.getDeclaredField(key);
            field.set(mapParam,val);
        }
    }

    public MapParam getMapParam(){
        return mapParam;
    }
}

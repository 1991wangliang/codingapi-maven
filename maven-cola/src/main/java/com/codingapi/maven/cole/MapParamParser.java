package com.codingapi.maven.cole;

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
        str = str.replaceFirst("\\@","");

        String[] params = str.split("\\@");

        for(String param:params){
            String[] kvs = param.split(":");
            if(kvs!=null&&kvs.length==2) {
                String key = kvs[0].trim();
                String valueString = kvs[1].trim();
                put(key, valueString);
            }
        }

        return this;
    }


    private void put(String key, String val) {
        if(val!=null&&!"".equals(val)){
            try {
                Field field = MapParam.class.getDeclaredField(key);
                field.set(mapParam, val);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public MapParam getMapParam(){
        return mapParam;
    }
}

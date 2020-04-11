package com.codingapi.maven.cole;

import java.util.Arrays;
import java.util.List;

public class JavaDocParser {

    private List<String> lines;

    public JavaDocParser(String context) {
        this.lines = Arrays.asList(context.split("\n"));
    }

    public Markdown parser() {
        MapParam mapParam = new MapParam();
        Markdown markdown = new Markdown();
        StringBuilder stringBuilder = new StringBuilder();
        for(String line:lines){
           LineParser lineParser =  new LineParser(line);
           MapParamParser mapParamParser = new MapParamParser(mapParam);
           if(lineParser.isParam()){
               mapParamParser.parser(line);
               continue;
           }
           if(lineParser.isTitle()){
               markdown.setTitle(line);
               continue;
           }
           if(lineParser.isSubTitle()){
               markdown.setSubtitle(line);
               continue;
           }
           stringBuilder.append(line);
           stringBuilder.append("    \n");
        }
        markdown.setContent(stringBuilder.toString());
        markdown.putMapParam(mapParam);
        return markdown;
    }

    public class LineParser {

        private String line;

        public LineParser(String line) {
            this.line = line;
        }

        public boolean isParam(){
            return (line.startsWith("{")&&line.endsWith("}"));
        }

        public boolean isTitle(){
            return line.startsWith("# ");
        }

        public boolean isSubTitle(){
            return line.startsWith("> ");
        }

    }
}

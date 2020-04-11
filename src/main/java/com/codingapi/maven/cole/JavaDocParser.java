package com.codingapi.maven.cole;

import java.util.Arrays;
import java.util.List;

public class JavaDocParser {

    private List<String> lines;

    public JavaDocParser(String context) {
        this.lines = Arrays.asList(context.split("\n"));
    }

    public Markdown parser() {
        try {
            Markdown markdown = new Markdown();
            MapParamParser mapParamParser = new MapParamParser();
            StringBuilder stringBuilder = new StringBuilder();
            for(String line:lines){
                line = line.trim();
                LineParser lineParser =  new LineParser(line);
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
            markdown.putMapParam(mapParamParser.getMapParam());
            return markdown;
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
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

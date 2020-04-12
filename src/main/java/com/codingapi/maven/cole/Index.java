package com.codingapi.maven.cole;

import org.aspectj.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Index {

    private List<Markdown> markdowns;

    private StringBuilder content = new StringBuilder();

    public Index(List<Markdown> markdowns) {
        this.markdowns = markdowns;
    }

    public void write(String path){
            title();
            time();
            index();
        File file = new File(path+"/index.md");
        FileUtil.writeAsString(file,content.toString());
        System.out.println("see markdown:"+file.getAbsolutePath());
    }

    private void index(){
        MarkdownType[] types = MarkdownType.values();

        for(MarkdownType type:types){
            content.append(type);
            br();
            for(Markdown markdown:markdowns){
                if(markdown.getType().equals(type)){
                    content.append(String.format("* [%s](%s)",markdown.getTitle(),markdown.getIndexUrl()));
                    br();
                }
            }
        }
    }


    private void title(){
        content.append("# index");
        br();
    }

    private void time(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        content.append(simpleDateFormat.format(date));
        br();
    }

    private void br(){
        content.append("    \n");
    }

}

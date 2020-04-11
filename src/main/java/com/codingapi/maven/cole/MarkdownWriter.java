package com.codingapi.maven.cole;

import org.aspectj.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MarkdownWriter {

    private Markdown markdown;
    private List<Link> links;
    private StringBuilder content;

    public MarkdownWriter(Markdown markdown, List<Link> links) {
        this.markdown = markdown;
        this.links = links;
        this.content = new StringBuilder();
    }

    public void write(){
        append(markdown.getTitle());
        br();
        append(markdown.getSubtitle());
        br();
        table();
        br();
        link();
        br();
        append(markdown.getContent());
    }

    private void link() {
        if(markdown.getLinks()!=null&&markdown.getLinks().size()>0) {
            append("links:");
            for (String link : markdown.getLinks()) {
                Link leader = getLink(link);
                if (leader != null) {
                    append("[" + leader.getTitle() + "](" + leader.getUrl() + ")  ");
                }
            }
        }
    }

    private Link getLink(String link){
        for(Link item:links){
            if(item.getName().equals(link)){
                return item;
            }
        }
        return null;
    }

    private void table(){
        append("|  author   | time  |    \n");
        append("|   ----    | ----  |    \n");
        append("|   "+(markdown.getAuthor()==null?"":markdown.getAuthor())
                +"    |  "+(markdown.getTime()==null?"":markdown.getTime())+"  |    \n");
    }

    private void br(){
        append("    \n");
    }

    private void append(String content){
        if(content!=null&&!"".equals(content)) {
            this.content.append(content);
        }
    }

    public void save(String path){
        File file = new File(path+"/"+markdown.getType()+"/"+markdown.getName()+".md");
        FileUtil.writeAsString(file,content.toString());
        System.out.println("see markdown:"+file);
    }


}

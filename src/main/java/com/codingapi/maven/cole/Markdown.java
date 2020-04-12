package com.codingapi.maven.cole;

import java.util.Arrays;
import java.util.List;

public class Markdown {

    private MarkdownType type;

    private String name;

    private String author;

    private String time;

    private List<String> links;

    private String content;

    private String subtitle;

    private String title;

    public Markdown(String name, MarkdownType markdownType) {
        this.name = name;
        this.type = markdownType;
        this.title = name;
    }

    public void putMapParam(MapParam mapParam) {
        this.setAuthor(mapParam.author);
        this.setTime(mapParam.time);
        if(mapParam.see!=null) {
            this.setLinks(Arrays.asList(mapParam.see.split(",")));
        }
    }

    public MarkdownType getType() {
        return type;
    }

    public void setType(MarkdownType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShowTitle(){
        if(title.startsWith("#")){
            return title.replaceAll("#","").trim();
        }
        return title;
    }


    public String getContent() {
        return content;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public List<String> getLinks() {
        return links;
    }

    public void print(){
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "Markdown{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", time='" + time + '\'' +
                ", links=" + links +
                ", content='" + content + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getUrl() {
        return "../"+type+"/"+name+".md";
    }


    public String getIndexUrl() {
        return type+"/"+name+".md";
    }
}

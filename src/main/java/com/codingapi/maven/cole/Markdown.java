package com.codingapi.maven.cole;

import java.util.Arrays;
import java.util.List;

public class Markdown {

    private String author;

    private String time;

    private int order;

    private List<String> links;

    private String content;

    private String subtitle;

    private String title;

    public void putMapParam(MapParam mapParam) {
        this.setAuthor(mapParam.author);
        this.setTime(mapParam.time);
        this.setOrder(Integer.parseInt(mapParam.order));
        this.setLinks(Arrays.asList(mapParam.see.split(",")));
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public int getOrder() {
        return order;
    }
    public List<String> getLinks() {
        return links;
    }

    public void print(){
        System.out.println(content);
    }
}

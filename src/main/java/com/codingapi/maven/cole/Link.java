package com.codingapi.maven.cole;

public class Link {

    private String name;
    private String url;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title!=null){
            title = title.replaceFirst("#","");
            title = title.trim();
        }
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

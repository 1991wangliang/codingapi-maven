package com.codingapi.maven.cole;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


public class JavaDocHelper {

    private static RootDoc rootDoc;

    public static class Doclet {
        public static boolean start(RootDoc rootDoc) {
            JavaDocHelper.rootDoc = rootDoc;
            return true;
        }
    }

    public static void init(String targetPath,String javaPath){
        List<String> params = new ArrayList<>();
        params.add("-doclet");
        params.add(JavaDocHelper.Doclet.class.getName());
        params.add("-encoding");
        params.add("utf-8");
        params.add("-classpath");
        params.add(targetPath);
        params.add(javaPath);
        com.sun.tools.javadoc.Main.execute(params.toArray(new String[]{}));
    }


    public static void show(Class<?> clazz, List<Markdown> markdowns, List<Link> links, Class<?extends Annotation> annotationClazz){
        ClassDoc[] classes = rootDoc.classes();
        for(ClassDoc classDoc : classes){
            String commentText = classDoc.commentText();
            JavaDocParser javaDocParser = new JavaDocParser(commentText);
            Markdown markdown =  javaDocParser.parser(clazz.getSimpleName(),MarkdownType.parser(annotationClazz));
            Link link = new Link();
            link.setTitle(markdown.getTitle());
            link.setName(markdown.getName());
            link.setUrl(markdown.getUrl());
            links.add(link);
            markdowns.add(markdown);
        }
    }



}
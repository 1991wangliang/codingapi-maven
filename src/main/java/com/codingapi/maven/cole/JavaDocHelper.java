package com.codingapi.maven.cole;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

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

    public static void init(String targetPath, List<String> javaList){
        List<String> params = new ArrayList<>();
        params.add("-doclet");
        params.add(JavaDocHelper.Doclet.class.getName());
        params.add("-encoding");
        params.add("utf-8");
        params.add("-classpath");
        params.add(targetPath);
        params.addAll(javaList);
        com.sun.tools.javadoc.Main.execute(params.toArray(new String[]{}));
    }


    public static void show(){
        ClassDoc[] classes = rootDoc.classes();
        for(ClassDoc classDoc : classes){
            String commentText = classDoc.commentText();
            JavaDocParser javaDocParser = new JavaDocParser(commentText);
            Markdown markdown =  javaDocParser.parser();
            markdown.print();
        }
    }


}
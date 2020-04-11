package com.codingapi.maven.code;

import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.executor.Executor;
import com.codingapi.maven.cole.JavaDocHelper;
import com.codingapi.maven.cole.Link;
import com.codingapi.maven.cole.Markdown;
import com.codingapi.maven.cole.MarkdownWriter;
import lombok.SneakyThrows;
import org.reflections.Reflections;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ColaMojoTest {

    private List<Markdown> markdowns = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    String outputMarkdown = "D://test/";
    String scannerPackage = "com.codingapi.cola.colademo.executor";
    File outputDirectory = new File("E:\\developer\\idea\\github\\COLA\\cola-demo\\target\\classes");
    File sourceDir = new File("E:\\developer\\idea\\github\\COLA\\cola-demo\\src\\main\\java");

    @SneakyThrows
    public void execute(){
        URL[] urls= new URL[]{outputDirectory.toURL()};
        Reflections reflections = new Reflections(scannerPackage, URLClassLoader.newInstance(urls));
        findClazz(reflections,Executor.class);
        findClazz(reflections, EventHandler.class);
        save();
    }

    private void findClazz(Reflections reflections, Class<?extends Annotation> clazzAnnotation){
        Set<Class<?>> classSet =  reflections.getTypesAnnotatedWith(clazzAnnotation);
        System.out.println("classSet:"+classSet.size());

        for (Class<?> clazz:classSet){
            String path = String.format("%s\\%s.java",sourceDir,clazz.getName().replaceAll("\\.","\\\\"));
            System.out.println("path:"+path);
            JavaDocHelper.init(outputDirectory.getAbsolutePath(),path);
            JavaDocHelper.show(clazz,markdowns,links);
        }
    }

    private void save(){
        for(Markdown markdown:markdowns) {
            MarkdownWriter markdownWriter = new MarkdownWriter(markdown,links);
            markdownWriter.write();
            markdownWriter.save(outputMarkdown);
        }
    }


    public static void main(String[] args) {
       ColaMojoTest colaMojoTest =  new ColaMojoTest();
       colaMojoTest.execute();
    }
}

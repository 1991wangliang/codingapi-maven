package com.codingapi.maven.cole;

import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.executor.Executor;
import lombok.SneakyThrows;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@Mojo(name = "cola",requiresDependencyResolution = ResolutionScope.COMPILE)
public class ColaMojo extends AbstractMojo {

    @Parameter
    private String scannerPackage;

    @Parameter
    private String outputMarkdown;

    @Parameter(defaultValue = "${project.build.outputDirectory}",required = true,readonly = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.sourceDirectory}",required = true,readonly = true)
    private File sourceDir;

    @Parameter(defaultValue = "${project}",readonly = true,required = true)
    private MavenProject project;

    private List<Markdown> markdowns = new ArrayList<>();

    private List<Link> links = new ArrayList<>();

    @SneakyThrows
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("sourceDir:"+sourceDir);
        getLog().info("scannerPackage:"+scannerPackage);
        getLog().info("outputDirectory:"+outputDirectory);
        getLog().info("outputMarkdown:"+outputMarkdown);

        List<URL> urlList = new ArrayList<>();
        urlList.add(outputDirectory.toURL());
        for(Object object:project.getCompileArtifacts()){
            Artifact artifact =(Artifact)object;
            urlList.add(artifact.getFile().toURL());
        }
        URL[] urls= urlList.toArray(new URL[]{});
        try {
            ClassLoader classLoader = new URLClassLoader(urls);
            Reflections reflections = new Reflections(scannerPackage, classLoader);
            findClazz(reflections, Executor.class);
            findClazz(reflections, EventHandler.class);
            resort();
            save();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resort(){
        Collections.sort(markdowns, new Comparator<Markdown>() {
            @Override
            public int compare(Markdown o1, Markdown o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });
    }

    private void findClazz(Reflections reflections, Class<?extends Annotation> clazzAnnotation){
        Set<Class<?>> classSet =  reflections.getTypesAnnotatedWith(clazzAnnotation);
        getLog().info(clazzAnnotation.getName()+"->classSet:"+classSet.size());

        for (Class<?> clazz:classSet){
            String path = String.format("%s/%s.java",sourceDir,clazz.getName().replaceAll("\\.","/"));
            getLog().info("path:"+path);
            JavaDocHelper.init(outputDirectory.getAbsolutePath(),path);
            JavaDocHelper.show(clazz,markdowns,links,clazzAnnotation);
        }
    }

    private void save(){
        for(Markdown markdown:markdowns) {
            MarkdownWriter markdownWriter = new MarkdownWriter(markdown,links);
            markdownWriter.write();
            markdownWriter.save(outputMarkdown);
        }
        Index index = new Index(markdowns);
        index.write(outputMarkdown);
    }

}

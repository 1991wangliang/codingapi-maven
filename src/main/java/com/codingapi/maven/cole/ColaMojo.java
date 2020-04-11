package com.codingapi.maven.cole;

import com.alibaba.cola.executor.Executor;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.reflections.Reflections;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

@Mojo(name = "cola",requiresDependencyResolution = ResolutionScope.COMPILE)
public class ColaMojo extends AbstractMojo {

    @Parameter
    private String scannerPackage;

    @Parameter
    private String outputMarkdown;

    @Parameter(defaultValue = "${basedir}")
    private File baseDir;

    @Parameter(defaultValue = "${project.build.outputDirectory}",required = true,readonly = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.sourceDirectory}",required = true,readonly = true)
    private File sourceDir;

    @SneakyThrows
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("scannerPackage:"+scannerPackage);
        getLog().info("outputMarkdown:"+outputMarkdown);
        URL[] urls= new URL[]{outputDirectory.toURL()};
        Reflections reflections = new Reflections(scannerPackage,URLClassLoader.newInstance(urls));
        Set<Class<?>> classSet =  reflections.getTypesAnnotatedWith(Executor.class);
        getLog().info("classSet:"+classSet.size());
        for (Class<?> clazz:classSet){
            String path = String.format("%s\\%s.java",sourceDir,clazz.getName().replaceAll("\\.","\\\\"));
            getLog().info("path:"+path);
            JavaDocHelper.init(outputDirectory.getAbsolutePath(),path);
            JavaDocHelper.show(clazz);
        }
    }

}

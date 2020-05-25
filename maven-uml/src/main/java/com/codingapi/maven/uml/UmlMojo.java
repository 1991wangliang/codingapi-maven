package com.codingapi.maven.uml;

import lombok.SneakyThrows;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "uml", requiresDependencyResolution = ResolutionScope.COMPILE)
public class UmlMojo extends AbstractMojo {

    @Parameter
    private String scannerPackage;

    @Parameter
    private String outputPath;

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
    private File sourceDir;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;


    @SneakyThrows
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("sourceDir:" + sourceDir);
        getLog().info("scannerPackage:" + scannerPackage);
        getLog().info("outputDirectory:" + outputDirectory);
        getLog().info("outputPath:" + outputPath);

        List<URL> urlList = new ArrayList<>();
        urlList.add(outputDirectory.toURL());
        for (Object object : project.getCompileArtifacts()) {
            Artifact artifact = (Artifact) object;
            urlList.add(artifact.getFile().toURL());
        }
        URL[] urls = urlList.toArray(new URL[]{});
        try {
            ClassLoader classLoader = new URLClassLoader(urls);
            new GeneratePlantUMLCli(
                    outputPath,
                    scannerPackage,
                    classLoader
            ).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

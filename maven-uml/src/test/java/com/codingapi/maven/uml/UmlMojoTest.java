package com.codingapi.maven.uml;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author lorne
 * @date 2020/5/27
 * @description
 */
class UmlMojoTest {

    private UmlMojo umlMojo = new UmlMojo();


    @Test
    @SneakyThrows
    void execute() {
//        [INFO] sourceDir:/Users/Shared/Previously Relocated Items/Security/developer/idea/gitee/evaluate/evaluate-domain/src/main/java
//        [INFO] scannerPackage:com.codingapi.evaluate.domain
//        [INFO] outputDirectory:/Users/Shared/Previously Relocated Items/Security/developer/idea/gitee/evaluate/evaluate-domain/target/classes
//        [INFO] outputPath:evaluate.puml

        File outputDirectory = new File("E:\\developer\\idea\\gitee\\evaluate\\evaluate-domain\\target\\classes");
        String outputPath = "evaluate.puml";
        String scannerPackage = "com.codingapi.evaluate.domain";
        umlMojo.run(outputDirectory,outputPath,scannerPackage);
    }


}
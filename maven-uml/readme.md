

## uml
uml 插件是编写领域模型plantuml的插件,使用方式如下:

```$xslt
  <build>
    <plugins>
      <plugin>
        <!-- https://github.com/1991wangliang/codingapi-maven -->
        <groupId>com.codingapi.maven</groupId>
        <artifactId>maven-uml</artifactId>
        <version>1.0.0</version>
        <!-- 设定在compile 编译时执行 -->
        <executions>
          <execution>
            <phase>compile</phase>
            <!-- 执行的mojo名称 cola -->
            <goals>
              <goal>uml</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <!--        扫码的包路径       -->
          <scannerPackage>com.codingapi.txlcn.tc</scannerPackage>
          <!-- markdown导出路径(相对路径) -->
          <outputPath>txlcn-uml.puml</outputPath>
        </configuration>
        <!--       插件执行时依赖的pom      -->
        <dependencies>
          <dependency>
            <groupId>io.github.classgraph</groupId>
            <artifactId>classgraph</artifactId>
            <version>4.8.78</version>
          </dependency>
        </dependencies>
      </plugin>
    </plugins>
  </build>

```


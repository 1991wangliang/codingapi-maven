# codingapi-maven

codingapi maven 插件,由于尚未上传到中心库,使用时限制性编译打包.`mvn clean install`

## cola
cola 插件是编写Executor业务文档的插件,使用方式如下:

```
  <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

            <plugin>
                <groupId>com.codingapi.maven</groupId>
                <artifactId>codingapi-maven-plugin</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <!--        扫码的包路径       -->
                    <scannerPackage>com.codingapi.cola.colademo</scannerPackage>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>com.alibaba.cola</groupId>
                        <artifactId>cola-core</artifactId>
                        <version>2.0.0</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```
执行命令
```
mvn clean package codingapi:cola
```


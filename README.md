# codingapi-maven

codingapi maven 插件,由于尚未上传到中心库,使用时限制性编译打包.`mvn clean install`

## cola
cola 插件是编写Executor业务文档的插件,使用方式如下:

```
     <build>
        <plugins>
            <plugin>
                <!-- https://github.com/1991wangliang/codingapi-maven -->
                <groupId>com.codingapi.maven</groupId>
                <artifactId>codingapi-maven-plugin</artifactId>
                <version>1.0.0</version>
                <!-- 设定在compile 编译时执行 -->
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <!-- 执行的mojo名称 cola -->
                        <goals>
                            <goal>cola</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <!--        扫码的包路径       -->
                    <scannerPackage>com.codingapi.tm.hqhbserver</scannerPackage>
                    <!-- markdown导出路径(相对路径) -->
                    <outputMarkdown>markdown</outputMarkdown>
                </configuration>
                 <!--       插件执行时依赖的pom      -->
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

注释编写规范  
支持的参数写法`{@see:value}`     
一行写多个`{@see:value@autor:lorne}`   
支持的字段有如下三种:  
@see 关联   
@autor 作者   
@time 时间  
```
package com.codingapi.cola.colademo.executor;


import com.alibaba.cola.executor.Executor;

/**
 * # 添加demo update
 * ##  添加业务的执行器
 * {@see:DemoAddCmdExe,DemoAddCmdExe,DemoAddCmdExe,DemoAddCmdExe,DemoAddCmdExe}
 * {@author:lorne @time:2019-04-12}
 * 这是一大段文字DemoAddCmdExe好的代码规范是一个程序员的基本修炼，好的代码注释更能体现一个程序员的思维逻辑，
 * 虽然代码是用来给机器运行的，我们只要能写出能让编译器运行的代码就行了，但是如果没有好的编码规范，到项目后期，
 * 加入开发的人员逐渐增多时，每个人的编码风格都不一样，这就会让项目维护者很难维护，所以开始就要制定一些好的规范
 * 来让大家遵守，这样才能写出可维护，健壮的项目，这就是接下来要做的事情。第一节从要从代码注释这一块说起，包含: 版
 * 权注释、类注释(Class)、构造函数注释(Constructor)、方法注释(Methods)、代码块注释(Block)、单句注释、字段名注释
 * ，然后分别为eclipse、IDEA创建注释模块等。
 * ```
 *     public String hello(){
 *         return "123";
 *     }
 *
 * ```
 */
@Executor
public class DemoUpdateCmdExe {


    public String hello(){
        return "123";
    }

}

```

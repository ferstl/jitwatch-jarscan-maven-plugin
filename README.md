# jitwatch-jarscan-maven-plugin
*- A Maven plugin for the JitWatch JarScan Utility*

The jitwatch-jarscan-maven-plugin integrates [jitwatch](https://github.com/AdoptOpenJDK/jitwatch)'s `JarScan` utility with Apache Maven.
It allows you to scan your projects' artifacts and their dependencies for large methods during a maven build.

### How to use it

#### Option 1: Integrate it into your Maven build
The plugin runs during the *verify* phase by default in order to be able to scan the artifact which was built in the preceding *package* phase.

    <plugin>
      <groupId>com.github.ferstl</groupId>
      <artifactId>jitwatch-jarscan-maven-plugin</artifactId>
      <version>1.0.0-SNAPSHOT</version>
      <configuration>
        <freqInlineSize>325</freqInlineSize>
        <reportFile>target/jarscan-report</reportFile>
        <analyzeDependencies>false</analyzeDependencies>
      </configuration>
      <executions>
        <execution>
          <id>crate-jarscan-report</id>
          <goals>
            <goal>scan</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

#### Option 2: Run it directly on your project

    mvn com.github.ferstl:jitwatch-jarscan-maven-plugin:scan
    
If you want to omit the group ID in the maven invocation, add it as [`<pluginGroup>`](https://maven.apache.org/settings.html#Plugin_Groups) to your settings.xml file.

### FAQ

Q: How does this Maven plugin scan the own artifact for a project
 
A: The plugin runs in the *verify* phase of the maven build by default. This phase is after the *package* phase in the build lifecycle where the JAR file for the project is crated. If you invoke the plugin directly you need to make sure to have a built project artifact in your `target` folder. The plugin will log a warning if no artifact is found.
 
Q: There are no releases or tags of [jitwatch](https://github.com/AdoptOpenJDK/jitwatch) at the moment. How do you integrate it?
 
A: JitWatch is added to this project as Git submodule. The maven build for this plugin will copy the classes for the `JarScan` utility and makes it part of this project.
 Take a look at the [Git repository](https://github.com/ferstl/jitwatch-jarscan-maven-plugin) of this project to see which revision of JitWatch is used.
 

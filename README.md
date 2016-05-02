# jitwatch-jarscan-maven-plugin
*- A Maven plugin for the JITWatch [JarScan Tool](https://github.com/AdoptOpenJDK/jitwatch/wiki/JarScan)*

[![Build Status](https://travis-ci.org/ferstl/jitwatch-jarscan-maven-plugin.svg?branch=master)](https://travis-ci.org/ferstl/jitwatch-jarscan-maven-plugin) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ferstl/jitwatch-jarscan-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ferstl/jitwatch-jarscan-maven-plugin)

The jitwatch-jarscan-maven-plugin integrates [jitwatch](https://github.com/AdoptOpenJDK/jitwatch)'s [JarScan Tool](https://github.com/AdoptOpenJDK/jitwatch/wiki/JarScan) with Apache Maven.
It allows you to scan your projects' artifacts and their dependencies for large methods during a Maven build.

Please refer to the [plugin documentation](http://ferstl.github.io/jitwatch-jarscan-maven-plugin/plugin-info.html) for all configuration options of this plugin.

## How to use it

The plugin is available on Maven Central. So no further repository configuration is required.

### Option 1: Integrate it into your Maven build
The plugin runs during the *verify* phase by default in order to be able to scan the artifact which was built in the preceding *package* phase.

    <plugin>
      <groupId>com.github.ferstl</groupId>
      <artifactId>jitwatch-jarscan-maven-plugin</artifactId>
      <version>1.0.1</version>
      <executions>
        <execution>
          <id>jarscan</id>
          <goals>
            <goal>scan</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

### Option 2: Run it directly on your project

    mvn com.github.ferstl:jitwatch-jarscan-maven-plugin:scan
    
If you add `com.github.ferstl` as [`<pluginGroup>`](https://maven.apache.org/settings.html#Plugin_Groups) to your `settings.xml` file, you can just run:

    mvn jarscan:scan

## Build

Build the Plugin:

    # Use --recursive for the submodule
    git clone --recursive git@github.com:ferstl/jitwatch-jarscan-maven-plugin.git
    mvn install

Create and deploy the plugin documentation:

    mvn site-deploy
    
Create the Release:

    mvn release:prepare -DpreparationGoals="clean deploy site-deploy"

(`mvn release:prepare release:perform` does not work because the jitwatch sumodule is not checked out during `release:perform`)

## FAQ

Q: Where is the plugin documentation?

A: Here: http://ferstl.github.io/jitwatch-jarscan-maven-plugin/plugin-info.html

-----

Q: How does this Maven plugin scan the own artifact for a project
 
A: The plugin runs in the *verify* phase of the Maven build by default. This phase is after the *package* phase in the build lifecycle where the JAR file for the project is crated. If you invoke the plugin directly you need to make sure to have a built project artifact in your `target` folder. The plugin will log a warning if no artifact is found.

-----
 
Q: There are no releases or tags of [jitwatch](https://github.com/AdoptOpenJDK/jitwatch) at the moment. How do you integrate it?
 
A: JITWatch is added to this project as Git submodule. The Maven build for this plugin will copy the classes for the JarScan Tool and makes it part of this project.
 Take a look at the [Git repository](https://github.com/ferstl/jitwatch-jarscan-maven-plugin) of this project to see which revision of JITWatch is used.
 

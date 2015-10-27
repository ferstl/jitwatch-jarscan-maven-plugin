package com.github.ferstl.jarscan;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;

import org.adoptopenjdk.jitwatch.jarscan.JarScan;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(
    name = "scan",
    aggregator = false,
    defaultPhase = LifecyclePhase.NONE,
    inheritByDefault = false,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class JarScanMojo extends AbstractMojo {

  @Component
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    @SuppressWarnings("unchecked")
    Set<Artifact> dependencies = this.project.getDependencyArtifacts();
    for (Artifact dependency : dependencies) {
      try {
        System.out.println("Scanning " + dependency);
        JarScan.iterateJar(dependency.getFile(), 325, new PrintWriter(new OutputStreamWriter(System.out)));
        System.out.println("done: " + dependency);
        System.out.println();
      } catch (IOException e) {
        throw new MojoExecutionException(e.getMessage());
      }
    }
  }

}

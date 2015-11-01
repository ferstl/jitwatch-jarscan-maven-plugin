/*
 * Copyright (c) 2015, Stefan Ferstl
 * Licensed under https://raw.githubusercontent.com/ferstl/jitwatch-jarscan-maven-plugin/master/LICENSE
 */
package com.github.ferstl.jarscan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.adoptopenjdk.jitwatch.jarscan.JarScan;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Scans the project's artifact and, if enabled, it's dependencies for large methods using JitWatch's JarScan utility.
 */
@Mojo(
    name = "scan",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class JarScanMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  /**
   * The value of {@code XX:FreqInlineSize} option. The default is 325.
   *
   * @since 1.0.0
   */
  @Parameter(property = "freqInlineSize", defaultValue = "325")
  private int freqInlineSize;

  /**
   * The path of the report file. If not set the report is written to the console.
   *
   * @since 1.0.0
   */
  @Parameter(property = "reportFile")
  private File reportFile;

  /**
   * Analyze dependency of the project.
   *
   * @since 1.0.0
   */
  @Parameter(property = "analyzeDependencies", defaultValue = "false")
  private boolean analyzeDependencies;

  /**
   * The scope of the artifacts that should be included. Only relevant when {@code analyzeDependencies=true}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "scope")
  private String scope;

  /**
   * List of artifacts to be included in the form of {@code groupId:artifactId:type:classifier}. Only relevant when
   * {@code analyzeDependencies=true}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "includes", defaultValue = "")
  private List<String> includes;

  /**
   * List of artifacts to be excluded in the form of {@code groupId:artifactId:type:classifier}. Only relevant when
   * {@code analyzeDependencies=true}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "excludes", defaultValue = "")
  private List<String> excludes;

  @Override
  public void execute() throws MojoExecutionException {
    analyzeOwnArtifact();

    if (this.analyzeDependencies) {
      analyzeDependencies();
    }
  }

  private void analyzeOwnArtifact() throws MojoExecutionException {
    // Ignore parent projects
    if (!"pom".equals(this.project.getPackaging())) {
      String buildDirectory = this.project.getBuild().getDirectory();
      String finalName = this.project.getBuild().getFinalName();

      Path jarFile = Paths.get(buildDirectory, finalName + ".jar");
      if (Files.exists(jarFile)) {
        printReport(this.project.getArtifact().toString(), jarFile.toFile());
      } else {
        getLog().warn("JAR file not found: " + jarFile);
      }
    }
  }

  private void analyzeDependencies() throws MojoExecutionException {
    Set<Artifact> dependencies = this.project.getDependencyArtifacts();

    ArtifactFilter filter = createArtifactFilter();

    for (Artifact dependency : dependencies) {
      if (filter.include(dependency)) {
        getLog().debug("Analyzing " + dependency);
        printReport(dependency.toString(), dependency.getFile());
      }
    }
  }

  private void printReport(String name, File file) throws MojoExecutionException {
    try (PrintWriter writer = createReportWriter()) {
      writer.println("Artifact: " + name);
      JarScan.iterateJar(file, this.freqInlineSize, writer);
      writer.println();
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage());
    }
  }

  private ArtifactFilter createArtifactFilter() {
    List<ArtifactFilter> filters = new ArrayList<>(3);

    if (this.scope != null) {
      filters.add(new ScopeArtifactFilter(this.scope));
    }

    if (!this.includes.isEmpty()) {
      filters.add(new StrictPatternIncludesArtifactFilter(this.includes));
    }

    if (!this.excludes.isEmpty()) {
      filters.add(new StrictPatternExcludesArtifactFilter(this.excludes));
    }

    return new AndArtifactFilter(filters);
  }

  private PrintWriter createReportWriter() throws IOException {
    if (this.reportFile != null) {
      BufferedWriter bw = Files.newBufferedWriter(this.reportFile.toPath(), StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
      return new PrintWriter(bw);
    }

    return new PrintWriter(new OutputStreamWriter(System.out)) {

      @Override
      public void close() { /* NOP */ }
    };
  }

}

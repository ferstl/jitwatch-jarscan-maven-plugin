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
import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.JarScan;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

abstract class AbstractJarScanMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  /**
   * The path of the report file. If not set the report is written to the console.
   *
   * @since 1.0.0
   */
  @Parameter(property = "reportFile")
  private File reportFile;

  /**
   * List of package name prefixes to analyze.
   *
   * @since 1.1
   */
  @Parameter(property = "packages", defaultValue = "")
  private List<String> packages;

  /**
   * Analyze the dependencies of the project.
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
  public void execute() throws MojoExecutionException, MojoFailureException {
    String errorMessage = validateMojoParameters();
    if (errorMessage != null) {
      throw new MojoFailureException(errorMessage);
    }

    analyzeOwnArtifact();

    if (this.analyzeDependencies) {
      analyzeDependencies();
    }
  }

  protected abstract IJarScanOperation createOperation();

  /**
   * Validates the mojo parameters and returns an error message in case the validation fails.
   *
   * @return An error message describing the problem in case the mojo parameters are not correct, {@code null} else.
   */
  protected String validateMojoParameters() {
    return null;
  }

  private void analyzeOwnArtifact() throws MojoExecutionException {
    // Ignore parent projects
    if (!"pom".equals(this.project.getPackaging())) {
      String buildDirectory = this.project.getBuild().getDirectory();
      String finalName = this.project.getBuild().getFinalName();

      Path jarFile = Paths.get(buildDirectory, finalName + ".jar");
      if (Files.exists(jarFile)) {
        scanAndprintReport(this.project.getArtifact().toString(), jarFile.toFile());
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
        scanAndprintReport(dependency.toString(), dependency.getFile());
      }
    }
  }

  private void scanAndprintReport(String name, File file) throws MojoExecutionException {
    IJarScanOperation operation = createOperation();
    JarScan jarScan = new JarScan(operation);
    for (String prefix : this.packages) {
      prefix = prefix.replace("*", "");
      jarScan.addAllowedPackagePrefix(prefix);
    }

    try (PrintWriter writer = createReportWriter()) {
      jarScan.iterateJar(file);
      writer.println("Artifact: " + name);
      writer.println(operation.getReport());
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
      public void close() {
        flush();
      }
    };
  }
}

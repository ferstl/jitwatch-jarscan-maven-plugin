package com.github.ferstl.jarscan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.adoptopenjdk.jitwatch.jarscan.JarScan;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
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

  @Parameter(property = "freqInlineSize", defaultValue = "325")
  private int freqInlineSize;

  /**
   * The scope of the artifacts that should be included.
   *
   * @since 1.0.0
   */
  @Parameter(property = "scope")
  private String scope;

  /**
   * Comma-separated list of artifacts to be included in the form of {@code groupId:artifactId:type:classifier}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "includes", defaultValue = "")
  private List<String> includes;

  /**
   * Comma-separated list of artifacts to be excluded in the form of {@code groupId:artifactId:type:classifier}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "excludes", defaultValue = "")
  private List<String> excludes;

  /**
   * The path of the report file. If not set the report is written to the console.
   *
   * @since 1.0.0
   */
  @Parameter(property = "reportFile")
  private File reportFile;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    @SuppressWarnings("unchecked")
    Set<Artifact> dependencies = this.project.getDependencyArtifacts();

    ArtifactFilter filter = createArtifactFilter();

    for (Artifact dependency : dependencies) {
      if (filter.include(dependency)) {
        try (PrintWriter writer = createReportWriter()){
          System.out.println("Scanning " + dependency);
          JarScan.iterateJar(dependency.getFile(), this.freqInlineSize, writer);
          System.out.println("done: " + dependency);
          System.out.println();
        } catch (IOException e) {
          throw new MojoExecutionException(e.getMessage());
        }
      }
    }
  }

  private PrintWriter createReportWriter() throws IOException{
    if (this.reportFile != null) {
      BufferedWriter bw = Files.newBufferedWriter(this.reportFile.toPath(), StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
      return new PrintWriter(bw);
    }

    return new PrintWriter(new OutputStreamWriter(System.out)) {
      @Override
      public void close() { /* NOP */ }
    };
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

}

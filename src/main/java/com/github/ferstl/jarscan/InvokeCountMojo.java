package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.invokecount.InvokeCountOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Count the most called methods for each invoke instruction.
 *
 * @since 1.1
 */
@Mojo(
    name = "invokeCount",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class InvokeCountMojo extends AbstractJarScanMojo {

  /**
   * Limit to top n results per invoke type.
   *
   * @since 1.1
   */
  @Parameter(property = "limit", defaultValue = "0")
  private int limit;

  @Override
  protected IJarScanOperation createOperation() {
    return new InvokeCountOperation(this.limit);
  }

}

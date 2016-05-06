package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.nextinstruction.NextInstructionOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * List the most popular next instruction for each bytecode instruction.
 *
 * @since 1.1
 */
@Mojo(
    name = "nextInstructionFreq",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class NextInstructionFreqMojo extends AbstractJarScanMojo {

  /**
   * Limit to top n results per instruction.
   *
   * @since 1.1
   */
  @Parameter(property = "limit", defaultValue = "0")
  private int limit;

  @Override
  protected String validateMojoParameters() {
    if (this.limit < 0) {
      return "limit must be >= 0.";
    }

    return null;
  }

  @Override
  protected IJarScanOperation createOperation() {
    return new NextInstructionOperation(this.limit);
  }

}

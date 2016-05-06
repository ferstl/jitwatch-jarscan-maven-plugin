package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.sequencesearch.SequenceSearchOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * List methods containing the specified bytecode sequence.
 *
 * @since 1.1
 */
@Mojo(
    name = "sequenceSearch",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class SequenceSearchMojo extends AbstractJarScanMojo {

  /**
   * Comma separated sequence of bytecode instructions.
   *
   * @since 1.1
   */
  @Parameter(property = "sequence", defaultValue = "")
  private String sequence;

  @Override
  protected IJarScanOperation createOperation() {
    return new SequenceSearchOperation(this.sequence);
  }

}

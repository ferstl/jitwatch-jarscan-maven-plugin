package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.freqinlinesize.FreqInlineSizeOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;


/**
 * List every method with bytecode larger than specified limit.
 */
@Mojo(
    name = "maxMethodSize",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class MaxMethodSizeMojo extends AbstractJarScanMojo {

  /**
   * Report methods larger than the specified bytes. The default for {@code -XX:MaxInlineSize} is 35. The default for
   * {@code -XX:FreqInlineSize} is 325.
   *
   * @since 1.1.0
   */
  @Parameter(property = "limit", defaultValue = "0")
  private int limit;

  @Override
  protected IJarScanOperation createOperation() {
    return new FreqInlineSizeOperation(this.limit);
  }

}

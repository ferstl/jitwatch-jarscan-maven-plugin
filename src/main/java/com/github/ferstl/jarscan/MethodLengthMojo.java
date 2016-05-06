package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.methodlength.MethodLengthOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * List methods of the given bytecode size.
 *
 * @since 1.1
 */
@Mojo(
    name = "methodlength",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class MethodLengthMojo extends AbstractJarScanMojo {

  /**
   * Size of methods to find.
   *
   * @since 1.1
   */
  @Parameter(property = "length", defaultValue = "1")
  private int length;

  @Override
  protected String validateMojoParameters() {
    if (this.length <= 0) {
      return "length must be > 0.";
    }

    return null;
  }

  @Override
  protected IJarScanOperation createOperation() {
    return new MethodLengthOperation(this.length);
  }

}

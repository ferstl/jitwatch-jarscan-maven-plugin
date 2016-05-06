/*
 * Copyright (c) 2015-2016, Stefan Ferstl
 * Licensed under https://raw.githubusercontent.com/ferstl/jitwatch-jarscan-maven-plugin/master/LICENSE
 */
package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.freqinlinesize.FreqInlineSizeOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;


/**
 * List every method with bytecode larger than specified limit.
 *
 * @since 1.1
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
   * @since 1.1
   */
  @Parameter(property = "limit", defaultValue = "1")
  private int limit;

  @Override
  protected String validateMojoParameters() {
    if (this.limit <= 0) {
      return "limit must be > 0.";
    }

    return null;
  }

  @Override
  protected IJarScanOperation createOperation() {
    return new FreqInlineSizeOperation(this.limit);
  }

}

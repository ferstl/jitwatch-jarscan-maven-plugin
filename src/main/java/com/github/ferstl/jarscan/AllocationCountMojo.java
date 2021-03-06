/*
 * Copyright (c) 2015-2016, Stefan Ferstl
 * Licensed under https://raw.githubusercontent.com/ferstl/jitwatch-jarscan-maven-plugin/master/LICENSE
 */
package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.allocationcount.AllocationCountOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Count the most allocated types.
 *
 * @since 1.1
 */
@Mojo(
    name = "allocationCount",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class AllocationCountMojo extends AbstractJarScanMojo {

  /**
   * Limit to top n results.
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
    return new AllocationCountOperation(this.limit);
  }


}

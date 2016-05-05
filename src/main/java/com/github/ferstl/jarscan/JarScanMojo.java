/*
 * Copyright (c) 2015, Stefan Ferstl
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
 * Scans the project's artifact and, if enabled, it's dependencies for large methods using JITWatch's JarScan utility.
 *
 * @deprecated Use the goal {@code maxMethodSize} with {@code -Dlimit=325} or your previously chosen
 * {@link #freqInlineSize}.
 */
@Mojo(
    name = "scan",
    aggregator = false,
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDependencyResolution = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
@Deprecated
public class JarScanMojo extends AbstractJarScanMojo {

  /**
   * The value of the {@code -XX:FreqInlineSize} option. The default is 325.
   *
   * @since 1.0.0
   */
  @Parameter(property = "freqInlineSize", defaultValue = "325")
  private int freqInlineSize;

  @Override
  protected IJarScanOperation createOperation() {
    return new FreqInlineSizeOperation(this.freqInlineSize);
  }
}

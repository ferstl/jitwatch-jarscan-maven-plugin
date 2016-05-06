/*
 * Copyright (c) 2015-2016, Stefan Ferstl
 * Licensed under https://raw.githubusercontent.com/ferstl/jitwatch-jarscan-maven-plugin/master/LICENSE
 */
package com.github.ferstl.jarscan;

import org.adoptopenjdk.jitwatch.jarscan.IJarScanOperation;
import org.adoptopenjdk.jitwatch.jarscan.sequencesearch.SequenceSearchOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.StringUtils;

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
  @Parameter(property = "sequence", required = true)
  private String sequence;

  @Override
  protected String validateMojoParameters() {
    if (StringUtils.isBlank(this.sequence)) {
      return "sequence must not be empty.";
    }

    return null;
  }

  @Override
  protected IJarScanOperation createOperation() {
    return new SequenceSearchOperation(this.sequence);
  }

}

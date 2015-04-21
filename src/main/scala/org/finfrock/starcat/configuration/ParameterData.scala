package org.finfrock.starcat.configuration


/**
 *  We include this class to provide centralized access to
 *  all the magic numbers in the code. There is a commitment
 *  (unavoidable) to some of the design decisions in the core
 *  here (for example the data here are specific to the
 *  RegularPulse). This kind of commitment is ultimately
 *  going to show up somewhere, better to have it in as few
 *  places as possible.
 *
 */
object ParameterData {
  //Defaults provided here but possibly overridden by initialize methods

  // Adaptive Execute variables and methods
  private var workspaceBehaviorAdaptiveExecute = false;
  private var workspaceControlAdaptiveExecute = false;
  private var coderackBehaviorAdaptiveExecute = false;
  private var coderackControlAdaptiveExecute = false;
  private var slipnetBehaviorAdaptiveExecute = false;
  private var slipnetControlAdaptiveExecute = false;

  def getSlipnetBehaviorAdaptiveExecute() = slipnetBehaviorAdaptiveExecute

  def getWorkspaceBehaviorAdaptiveExecute() = workspaceBehaviorAdaptiveExecute

  def getCoderackBehaviorAdaptiveExecute() = coderackBehaviorAdaptiveExecute

  def getSlipnetControlAdaptiveExecute() = slipnetControlAdaptiveExecute

  def getWorkspaceControlAdaptiveExecute() = workspaceControlAdaptiveExecute

  def getCoderackControlAdaptiveExecute() = coderackControlAdaptiveExecute

  def initializeSlipnetBehaviorAdaptiveExecute(adaptiveExecute: Boolean) {
    slipnetBehaviorAdaptiveExecute = adaptiveExecute;
  }

  def initializeCoderackBehaviorAdaptiveExecute(adaptiveExecute: Boolean) {
    coderackBehaviorAdaptiveExecute = adaptiveExecute;
  }

  def initializeWorkspaceBehaviorAdaptiveExecute(adaptiveExecute: Boolean) {
    workspaceBehaviorAdaptiveExecute = adaptiveExecute;
  }

  def initializeSlipnetControlAdaptiveExecute(adaptiveExecute: Boolean) {
    slipnetControlAdaptiveExecute = adaptiveExecute;
  }

  def initializeCoderackControlAdaptiveExecute(adaptiveExecute: Boolean) {
    coderackControlAdaptiveExecute = adaptiveExecute;
  }

  def initializeWorkspaceControlAdaptiveExecute(adaptiveExecute: Boolean) {
    workspaceControlAdaptiveExecute = adaptiveExecute;
  }

  // Execute Factor variables and methods
  private var workspaceBehaviorExecuteFactor = 10;
  private var workspaceControlExecuteFactor = 1;
  private var coderackBehaviorExecuteFactor = 10;
  private var coderackControlExecuteFactor = 1;
  private var slipnetBehaviorExecuteFactor = 10;
  private var slipnetControlExecuteFactor = 1;

  def getSlipnetBehaviorExecuteFactor(): Int = slipnetBehaviorExecuteFactor
  def getWorkspaceBehaviorExecuteFactor(): Int = workspaceBehaviorExecuteFactor
  def getCoderackBehaviorExecuteFactor(): Int = coderackBehaviorExecuteFactor

  def getSlipnetControlExecuteFactor(): Int = slipnetControlExecuteFactor
  def getWorkspaceControlExecuteFactor(): Int = workspaceControlExecuteFactor
  def getCoderackControlExecuteFactor(): Int = coderackControlExecuteFactor

  def setSlipnetBehaviorExecuteFactor(execFactor: Int) {
    slipnetBehaviorExecuteFactor = execFactor
  }
  def setWorkspaceBehaviorExecuteFactor(execFactor: Int) {
    workspaceBehaviorExecuteFactor = execFactor
  }
  def setCoderackBehaviorExecuteFactor(execFactor: Int) {
    coderackBehaviorExecuteFactor = execFactor
  }

  //executeFactor is one of those rare data items that gets changed by the running code
  def setSlipnetControlExecuteFactor(execFactor: Int) {
    slipnetControlExecuteFactor = execFactor
  }

  def setWorkspaceControlExecuteFactor(execFactor: Int) {
    workspaceControlExecuteFactor = execFactor
  }

  def setCoderackControlExecuteFactor(execFactor: Int) {
    coderackControlExecuteFactor = execFactor
  }

  def initializeSlipnetBehaviorExecuteFactor(execFactor: Int) {
    slipnetBehaviorExecuteFactor = execFactor;
  }

  def initializeCoderackBehaviorExecuteFactor(execFactor: Int) {
    coderackBehaviorExecuteFactor = execFactor;
  }

  def initializeWorkspaceBehaviorExecuteFactor(execFactor: Int) {
    workspaceBehaviorExecuteFactor = execFactor;
  }

  def initializeSlipnetControlExecuteFactor(execFactor: Int) {
    slipnetControlExecuteFactor = execFactor;
  }

  def initializeCoderackControlExecuteFactor(execFactor: Int) {
    coderackControlExecuteFactor = execFactor;
  }

  def initializeWorkspaceControlExecuteFactor(execFactor: Int) {
    workspaceControlExecuteFactor = execFactor;
  }

  // Reduction Factor variables and methods
  private var workspaceBehaviorReductionFactor = 0.01;
  private var workspaceControlReductionFactor = 0.01;
  private var coderackBehaviorReductionFactor = 0.01;
  private var coderackControlReductionFactor = 0.01;
  private var slipnetBehaviorReductionFactor = 0.01;
  private var slipnetControlReductionFactor = 0.01;

  def getSlipnetBehaviorReductionFactor(): Double = slipnetBehaviorReductionFactor
  def getWorkspaceBehaviorReductionFactor(): Double = workspaceBehaviorReductionFactor
  def getCoderackBehaviorReductionFactor(): Double = coderackBehaviorReductionFactor

  def getSlipnetControlReductionFactor(): Double = slipnetControlReductionFactor
  def getWorkspaceControlReductionFactor(): Double = workspaceControlReductionFactor
  def getCoderackControlReductionFactor(): Double = coderackControlReductionFactor

  def initializeSlipnetBehaviorReductionFactor(reductionFactor: Double) {
    slipnetBehaviorReductionFactor = reductionFactor;
  }

  def initializeCoderackBehaviorReductionFactor(reductionFactor: Double) {
    coderackBehaviorReductionFactor = reductionFactor;
  }

  def initializeWorkspaceBehaviorReductionFactor(reductionFactor: Double) {
    workspaceBehaviorReductionFactor = reductionFactor;
  }

  def initializeSlipnetControlReductionFactor(reductionFactor: Double) {
    slipnetControlReductionFactor = reductionFactor;
  }

  def initializeCoderackControlReductionFactor(reductionFactor: Double) {
    coderackControlReductionFactor = reductionFactor;
  }

  def initializeWorkspaceControlReductionFactor(reductionFactor: Double) {
    workspaceControlReductionFactor = reductionFactor;
  }

  // sleeper variables and methods
  private var workspaceBehaviorSleeper = true;
  private var workspaceControlSleeper = true;
  private var coderackBehaviorSleeper = true;
  private var coderackControlSleeper = true;
  private var slipnetBehaviorSleeper = true;
  private var slipnetControlSleeper = true;

  def getSlipnetBehaviorSleeper() = slipnetBehaviorSleeper
  def getWorkspaceBehaviorSleeper() = workspaceBehaviorSleeper
  def getCoderackBehaviorSleeper() = coderackBehaviorSleeper

  def getSlipnetControlSleeper() = slipnetControlSleeper
  def getWorkspaceControlSleeper() = workspaceControlSleeper
  def getCoderackControlSleeper() = coderackControlSleeper

  def initializeSlipnetBehaviorSleeper(sleeper: Boolean) {
    slipnetBehaviorSleeper = sleeper;
  }

  def initializeCoderackBehaviorSleeper(sleeper: Boolean) {
    coderackBehaviorSleeper = sleeper;
  }

  def initializeWorkspaceBehaviorSleeper(sleeper: Boolean) {
    workspaceBehaviorSleeper = sleeper;
  }

  def initializeSlipnetControlSleeper(sleeper: Boolean) {
    slipnetControlSleeper = sleeper;
  }

  def initializeCoderackControlSleeper(sleeper: Boolean) {
    coderackControlSleeper = sleeper;
  }

  def initializeWorkspaceControlSleeper(sleeper: Boolean) {
    workspaceControlSleeper = sleeper;
  }

  // Sleep Time variables and methods
  private var workspaceBehaviorSleepTime = 10L
  private var workspaceControlSleepTime = 10L
  private var coderackBehaviorSleepTime = 10L
  private var coderackControlSleepTime = 10L
  private var slipnetBehaviorSleepTime = 100L
  private var slipnetControlSleepTime = 100L

  def getSlipnetBehaviorSleepTime(): Long = slipnetBehaviorSleepTime
  def getWorkspaceBehaviorSleepTime(): Long = workspaceBehaviorSleepTime
  def getCoderackBehaviorSleepTime(): Long = coderackBehaviorSleepTime

  def getSlipnetControlSleepTime(): Long = slipnetControlSleepTime
  def getWorkspaceControlSleepTime(): Long = workspaceControlSleepTime
  def getCoderackControlSleepTime(): Long = coderackControlSleepTime

  def initializeSlipnetBehaviorSleepTime(sleepTime: Long) {
    slipnetBehaviorSleepTime = sleepTime;
  }

  def initializeCoderackBehaviorSleepTime(sleepTime: Long) {
    coderackBehaviorSleepTime = sleepTime;
  }

  def initializeWorkspaceBehaviorSleepTime(sleepTime: Long) {
    workspaceBehaviorSleepTime = sleepTime;
  }

  def initializeSlipnetControlSleepTime(sleepTime: Long) {
    slipnetControlSleepTime = sleepTime;
  }

  def initializeCoderackControlSleepTime(sleepTime: Long) {
    coderackControlSleepTime = sleepTime;
  }

  def initializeWorkspaceControlSleepTime(sleepTime: Long) {
    workspaceControlSleepTime = sleepTime;
  }
}
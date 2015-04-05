package org.starcat.configuration

import org.starcat.core.Component
import org.starcat.slipnet.Slipnet
import org.starcat.workspace.Workspace
import org.starcat.coderack.Coderack

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
  
  def getBehaviorAdaptiveExecute(c:Component ):Boolean = {
    c match{
      case slipnet:Slipnet => slipnetBehaviorAdaptiveExecute
      case workspace:Workspace => workspaceBehaviorAdaptiveExecute
      case coderack:Coderack => coderackBehaviorAdaptiveExecute
      case _ => {
       System.out.println("Attempt to use an adaptiveExecute for a nonexistent component")
      false
      }
    }
  }
  
  def getControlAdaptiveExecute(c:Component):Boolean ={
     c match{
      case slipnet:Slipnet => slipnetControlAdaptiveExecute
      case workspace:Workspace => workspaceControlAdaptiveExecute
      case coderack:Coderack => coderackControlAdaptiveExecute
      case _ => {
       System.out.println("Attempt to use an adaptiveExecute for a nonexistent component")
      false
      }
    }
  }

  def initializeSlipnetBehaviorAdaptiveExecute(adaptiveExecute:Boolean) {
    slipnetBehaviorAdaptiveExecute = adaptiveExecute;
  }

  def initializeCoderackBehaviorAdaptiveExecute(adaptiveExecute:Boolean) {
    coderackBehaviorAdaptiveExecute = adaptiveExecute;
  }
  
  def initializeWorkspaceBehaviorAdaptiveExecute(adaptiveExecute:Boolean) {
    workspaceBehaviorAdaptiveExecute = adaptiveExecute;
  }
  
  def initializeSlipnetControlAdaptiveExecute(adaptiveExecute:Boolean) {
    slipnetControlAdaptiveExecute = adaptiveExecute;
  }

  def initializeCoderackControlAdaptiveExecute(adaptiveExecute:Boolean) {
    coderackControlAdaptiveExecute = adaptiveExecute;
  }
    
  def initializeWorkspaceControlAdaptiveExecute(adaptiveExecute:Boolean) {
    workspaceControlAdaptiveExecute = adaptiveExecute;
  }    
  
  // Execute Factor variables and methods
  private var workspaceBehaviorExecuteFactor = 10;
  private var workspaceControlExecuteFactor = 1;
  private var coderackBehaviorExecuteFactor = 10;
  private var coderackControlExecuteFactor = 1;
  private var slipnetBehaviorExecuteFactor = 10;
  private var slipnetControlExecuteFactor = 1;
  
  def getBehaviorExecuteFactor(c:Component):Int = {
     c match{
      case slipnet:Slipnet => slipnetBehaviorExecuteFactor
      case workspace:Workspace => workspaceBehaviorExecuteFactor
      case coderack:Coderack => coderackBehaviorExecuteFactor
      case _ => {
       System.out.println("Attempt to use an BehaviorExecuteFactor for a nonexistent component")
      0
      }
    }
  }
  
  def getControlExecuteFactor(c:Component):Int ={
     c match{
      case slipnet:Slipnet => slipnetControlExecuteFactor
      case workspace:Workspace => workspaceControlExecuteFactor
      case coderack:Coderack => coderackControlExecuteFactor
      case _ => {
       System.out.println("Attempt to use an executeFactor for a nonexistent component")
      0
      }
    }
  }

  //executeFactor is one of those rare data items that gets changed by the running code
  def setBehaviorExecuteFactor(c:Component, execFactor:Int)
  {
     c match{
      case slipnet:Slipnet => slipnetBehaviorExecuteFactor = execFactor
      case workspace:Workspace => workspaceBehaviorExecuteFactor = execFactor
      case coderack:Coderack => coderackBehaviorExecuteFactor = execFactor
      case _ => {
       println("Attempt to set an executeFactor for a nonexistent component")
      }
    }
  }

  //executeFactor is one of those rare data items that gets changed by the running code
  def setControlExecuteFactor(c:Component, execFactor:Int) {
     c match{
      case slipnet:Slipnet => slipnetControlExecuteFactor = execFactor
      case workspace:Workspace => workspaceControlExecuteFactor = execFactor
      case coderack:Coderack => coderackControlExecuteFactor = execFactor
      case _ => {
       System.out.println("Attempt to set an executeFactor for a nonexistent component");
      }
    }
  }  
  
  def initializeSlipnetBehaviorExecuteFactor(execFactor:Int) {
    slipnetBehaviorExecuteFactor = execFactor;
  }

  def initializeCoderackBehaviorExecuteFactor(execFactor:Int) {
    coderackBehaviorExecuteFactor = execFactor;
  }
  
  def initializeWorkspaceBehaviorExecuteFactor(execFactor:Int) {
    workspaceBehaviorExecuteFactor = execFactor;
  }
  
  def initializeSlipnetControlExecuteFactor(execFactor:Int) {
    slipnetControlExecuteFactor = execFactor;
  }

  def initializeCoderackControlExecuteFactor(execFactor:Int) {
    coderackControlExecuteFactor = execFactor;
  }
    
  def initializeWorkspaceControlExecuteFactor(execFactor:Int) {
    workspaceControlExecuteFactor = execFactor;
  }  
  
  // Reduction Factor variables and methods
  private var workspaceBehaviorReductionFactor = 0.01;
  private var workspaceControlReductionFactor = 0.01;
  private var coderackBehaviorReductionFactor = 0.01;
  private var coderackControlReductionFactor = 0.01;
  private var slipnetBehaviorReductionFactor = 0.01;
  private var slipnetControlReductionFactor = 0.01;
  
  def getBehaviorReductionFactor(c:Component):Double = {
     c match{
      case slipnet:Slipnet => slipnetBehaviorReductionFactor
      case workspace:Workspace => workspaceBehaviorReductionFactor
      case coderack:Coderack => coderackBehaviorReductionFactor
      case _ => {
       println("Attempt to use an ReductionFactor for a nonexistent component")
       0
      }
    }
  }
  
  def getControlReductionFactor(c:Component):Double = {
     c match{
      case slipnet:Slipnet => slipnetControlReductionFactor
      case workspace:Workspace => workspaceControlReductionFactor
      case coderack:Coderack => coderackControlReductionFactor
      case _ => {
       println("Attempt to use an ReductionFactor for a nonexistent component")
       0
      }
    }
  }

  def initializeSlipnetBehaviorReductionFactor(reductionFactor:Double) {
    slipnetBehaviorReductionFactor = reductionFactor;
  }

  def initializeCoderackBehaviorReductionFactor(reductionFactor:Double) {
    coderackBehaviorReductionFactor = reductionFactor;
  }
  
  def initializeWorkspaceBehaviorReductionFactor(reductionFactor:Double) {
    workspaceBehaviorReductionFactor = reductionFactor;
  }
  
  def initializeSlipnetControlReductionFactor(reductionFactor:Double) {
    slipnetControlReductionFactor = reductionFactor;
  }

  def initializeCoderackControlReductionFactor(reductionFactor:Double) {
    coderackControlReductionFactor = reductionFactor;
  }
    
  def initializeWorkspaceControlReductionFactor(reductionFactor:Double) {
    workspaceControlReductionFactor = reductionFactor;
  }    
    
  // sleeper variables and methods
  private var workspaceBehaviorSleeper = true;
  private var workspaceControlSleeper = true;
  private var coderackBehaviorSleeper = true;
  private var coderackControlSleeper = true;
  private var slipnetBehaviorSleeper = true;
  private var slipnetControlSleeper = true;
  
  def getBehaviorSleeper(c:Component):Boolean = {
     c match{
      case slipnet:Slipnet => slipnetBehaviorSleeper
      case workspace:Workspace => workspaceBehaviorSleeper
      case coderack:Coderack => coderackBehaviorSleeper
      case _ => {
       println("Attempt to use an Sleeper for a nonexistent component")
       false
      }
    }
  }
  
  def getControlSleeper(c:Component):Boolean = {
     c match{
      case slipnet:Slipnet => slipnetControlSleeper
      case workspace:Workspace => workspaceControlSleeper
      case coderack:Coderack => coderackControlSleeper
      case _ => {
       println("Attempt to use an Sleeper for a nonexistent component")
       false
      }
    }
  }

  def initializeSlipnetBehaviorSleeper(sleeper:Boolean) {
    slipnetBehaviorSleeper = sleeper;
  }

  def initializeCoderackBehaviorSleeper(sleeper:Boolean) {
    coderackBehaviorSleeper = sleeper;
  }
  
  def initializeWorkspaceBehaviorSleeper(sleeper:Boolean) {
    workspaceBehaviorSleeper = sleeper;
  }
  
  def initializeSlipnetControlSleeper(sleeper:Boolean) {
    slipnetControlSleeper = sleeper;
  }

  def initializeCoderackControlSleeper(sleeper:Boolean) {
    coderackControlSleeper = sleeper;
  }
    
  def initializeWorkspaceControlSleeper(sleeper:Boolean) {
    workspaceControlSleeper = sleeper;
  }  
  
  // Sleep Time variables and methods
  private var workspaceBehaviorSleepTime = 10L
  private var workspaceControlSleepTime = 10L
  private var coderackBehaviorSleepTime = 10L
  private var coderackControlSleepTime = 10L
  private var slipnetBehaviorSleepTime = 100L
  private var slipnetControlSleepTime = 100L
  
  def getBehaviorSleepTime(c:Component):Long ={
     c match{
      case slipnet:Slipnet => slipnetBehaviorSleepTime
      case workspace:Workspace => workspaceBehaviorSleepTime
      case coderack:Coderack => coderackBehaviorSleepTime
      case _ => {
       println("Attempt to use an SleepTime for a nonexistent component")
       10
      }
    }
  }
  
  def getControlSleepTime(c:Component):Long ={
     c match{
      case slipnet:Slipnet => slipnetControlSleepTime
      case workspace:Workspace => workspaceControlSleepTime
      case coderack:Coderack => coderackControlSleepTime
      case _ => {
       println("Attempt to use an SleepTime for a nonexistent component")
       10
      }
    }
  }

  def initializeSlipnetBehaviorSleepTime(sleepTime:Long) {
    slipnetBehaviorSleepTime = sleepTime;
  }

  def initializeCoderackBehaviorSleepTime(sleepTime:Long) {
    coderackBehaviorSleepTime = sleepTime;
  }
  
  def initializeWorkspaceBehaviorSleepTime(sleepTime:Long) {
    workspaceBehaviorSleepTime = sleepTime;
  }
  
  def initializeSlipnetControlSleepTime(sleepTime:Long) {
    slipnetControlSleepTime = sleepTime;
  }

  def initializeCoderackControlSleepTime(sleepTime:Long) {
    coderackControlSleepTime = sleepTime;
  }
    
  def initializeWorkspaceControlSleepTime(sleepTime:Long) {
    workspaceControlSleepTime = sleepTime;
  }   
}
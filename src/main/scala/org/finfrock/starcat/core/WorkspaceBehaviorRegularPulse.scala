package org.finfrock.starcat.core

import org.finfrock.starcat.util.CircularQueue
import org.finfrock.starcat.configuration.ParameterData
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Props

object WorkspaceBehaviorRegularPulse{
  def getProps(workspace:ActorRef):Props ={
    Props(classOf[WorkspaceBehaviorRegularPulse], workspace)
  }
}

class WorkspaceBehaviorRegularPulse(workspace: ActorRef)
  extends RegularPulseActor(workspace) {

  def isAdaptiveExecute(): Boolean = {
    return ParameterData.getWorkspaceBehaviorAdaptiveExecute()
  }

  def getExecuteFactor(): Int = {
    return ParameterData.getWorkspaceBehaviorExecuteFactor()
  }

  def getReductionFactor(): Double = {
    return ParameterData.getWorkspaceBehaviorReductionFactor()
  }

  def isSleeper(): Boolean = {
    return ParameterData.getWorkspaceBehaviorSleeper()
  }

  def getSleepTime(): Long = {
    return ParameterData.getWorkspaceBehaviorSleepTime()
  }

  def setExecuteFactor(execFactor: Int) {
    ParameterData.setWorkspaceBehaviorExecuteFactor(execFactor);
  }
  
  override def processAlgorithm() {
    while (!checkIfDoneProcessing()) {
      getCodeletToProcess() match {
        case Some(codeletToProcess) => process(codeletToProcess)
        case None                   => //do nothing
      }
    }
  }
}
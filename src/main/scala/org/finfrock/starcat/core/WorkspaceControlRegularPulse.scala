package org.finfrock.starcat.core

import org.finfrock.starcat.configuration.ParameterData
import org.finfrock.starcat.util.CircularQueue
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import org.finfrock.starcat.workspace.WorkspaceUpdateCodelet
import akka.actor.Props
import java.util.UUID

object WorkspaceControlRegularPulse{
  def getProps(workspace:ActorRef):Props ={
    Props(classOf[WorkspaceControlRegularPulse], workspace)
  }
}
class WorkspaceControlRegularPulse(slipnet: ActorRef) extends RegularPulseActor(slipnet) {

  def isAdaptiveExecute(): Boolean = {
    return ParameterData.getWorkspaceControlAdaptiveExecute()
  }
  def getExecuteFactor(): Int = {
    return ParameterData.getWorkspaceControlExecuteFactor()
  }
  def getReductionFactor(): Double = {
    return ParameterData.getWorkspaceControlReductionFactor()
  }
  def isSleeper(): Boolean = {
    return ParameterData.getWorkspaceControlSleeper()
  }
  def getSleepTime(): Long = {
    return ParameterData.getWorkspaceControlSleepTime()
  }

  def setExecuteFactor(execFactor: Int) {
    ParameterData.setWorkspaceControlExecuteFactor(execFactor)
  }

  override def preProcess() {
    val executes = getExecuteFactor()
    for (i <- 0 until executes) {
      val name = UUID.randomUUID().toString()
      val actorRef = context.system.actorOf(WorkspaceUpdateCodelet.getProps(), name)
      val newCodelet = new Codelet(name, actorRef, codeletType = Codelet.CONTROL_CODELET_TYPE)
      push(newCodelet)
    }
    super.preProcess()
  }
}
package org.finfrock.starcat.core

import org.finfrock.starcat.configuration.ParameterData
import org.finfrock.starcat.util.CircularQueue
import org.finfrock.starcat.coderack.CoderackUpdateCodelet
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import org.finfrock.starcat.workspace.WorkspaceUpdateCodelet
import org.finfrock.starcat.workspace.WorkspaceUpdateCodelet
import org.finfrock.starcat.coderack.CoderackUpdateCodelet
import akka.actor.Props
import java.util.UUID

object CoderackControlRegularPulse{
  def getProps(coderack:ActorRef):Props ={
    Props(classOf[CoderackControlRegularPulse], coderack)
  }
}
class CoderackControlRegularPulse(slipnet: ActorRef) extends RegularPulseActor(slipnet) {

  def isAdaptiveExecute(): Boolean = {
    return ParameterData.getCoderackControlAdaptiveExecute()
  }
  def getExecuteFactor(): Int = {
    return ParameterData.getCoderackControlExecuteFactor()
  }
  def getReductionFactor(): Double = {
    return ParameterData.getCoderackControlReductionFactor()
  }
  def isSleeper(): Boolean = {
    return ParameterData.getCoderackControlSleeper()
  }
  def getSleepTime(): Long = {
    return ParameterData.getCoderackControlSleepTime()
  }

  def setExecuteFactor(execFactor: Int) {
    ParameterData.setCoderackControlExecuteFactor(execFactor)
  }

  override def preProcess() {
    val executes = getExecuteFactor()

    for (i <- 0 until executes) {
      val name = UUID.randomUUID().toString()
      val actorRef = context.system.actorOf(CoderackUpdateCodelet.getProps(), name)
      val newCodelet = new Codelet(name, actorRef, codeletType = Codelet.CONTROL_CODELET_TYPE)
      push(newCodelet)
    }
    super.preProcess()
  }
}
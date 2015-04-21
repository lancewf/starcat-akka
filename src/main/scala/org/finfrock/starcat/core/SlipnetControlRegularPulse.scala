package org.finfrock.starcat.core

import org.finfrock.starcat.configuration.ParameterData
import org.finfrock.starcat.util.CircularQueue
import org.finfrock.starcat.slipnet.SlipnetUpdateCodelet
import org.finfrock.starcat.workspace.WorkspaceUpdateCodelet
import org.finfrock.starcat.coderack.CoderackUpdateCodelet
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Props
import java.util.UUID

object SlipnetControlRegularPulse{
  def getProps(slipnet:ActorRef):Props ={
    Props(classOf[SlipnetControlRegularPulse], slipnet)
  }
}
class SlipnetControlRegularPulse(slipnet:ActorRef) extends RegularPulseActor(slipnet) {
  
  def isAdaptiveExecute():Boolean = {
    return ParameterData.getSlipnetControlAdaptiveExecute()
  }
  def getExecuteFactor():Int = {
    return ParameterData.getSlipnetControlExecuteFactor()
  }
  def getReductionFactor():Double = {
    return ParameterData.getSlipnetControlReductionFactor()
  }
  def isSleeper():Boolean = {
    return ParameterData.getSlipnetControlSleeper()
  }
  def getSleepTime():Long =  {
    return ParameterData.getSlipnetControlSleepTime()
  }
  
  def setExecuteFactor(execFactor:Int) {
    ParameterData.setSlipnetControlExecuteFactor(execFactor)
  }

  override def preProcess() {
    val executes = getExecuteFactor()

    for (i <- 0 until executes) {
      val name = UUID.randomUUID().toString()
      val actorRef = context.system.actorOf(SlipnetUpdateCodelet.getProps(), name)
      val newCodelet = new Codelet(name, actorRef, codeletType = Codelet.CONTROL_CODELET_TYPE)
      push(newCodelet)
    }
    super.preProcess()
  } 
}
package org.finfrock.starcat.core

import org.finfrock.starcat.util.CircularQueue
import org.finfrock.starcat.configuration.ParameterData
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Props

object SlipnetBehaviorRegularPulse{
  def getProps(slipnet: ActorRef):Props = Props(classOf[SlipnetBehaviorRegularPulse], slipnet)
}

class SlipnetBehaviorRegularPulse(slipnet: ActorRef)
  extends RegularPulseActor(slipnet) {

  def isAdaptiveExecute(): Boolean = {
    return ParameterData.getSlipnetBehaviorAdaptiveExecute()
  }

  def getExecuteFactor(): Int = {
    return ParameterData.getSlipnetBehaviorExecuteFactor()
  }

  def getReductionFactor(): Double = {
    return ParameterData.getSlipnetBehaviorReductionFactor()
  }

  def isSleeper(): Boolean = {
    return ParameterData.getSlipnetBehaviorSleeper()
  }

  def getSleepTime(): Long = {
    return ParameterData.getSlipnetBehaviorSleepTime()
  }

  def setExecuteFactor(execFactor: Int) {
    ParameterData.setSlipnetBehaviorExecuteFactor(execFactor);
  }
}
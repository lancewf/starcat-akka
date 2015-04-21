package org.finfrock.starcat.core

import org.finfrock.starcat.util.CircularQueue
import org.finfrock.starcat.configuration.ParameterData
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import org.finfrock.starcat.coderack.CoderackActor
import akka.actor.Props

object CoderackBehaviorRegularPulse{
  def getProps(coderack:ActorRef):Props ={
    Props(classOf[CoderackBehaviorRegularPulse], coderack)
  }
}
class CoderackBehaviorRegularPulse(coderack: ActorRef)
  extends RegularPulseActor(coderack) {

  def isAdaptiveExecute(): Boolean = {
    return ParameterData.getCoderackBehaviorAdaptiveExecute()
  }

  def getExecuteFactor(): Int = {
    return ParameterData.getCoderackBehaviorExecuteFactor()
  }

  def getReductionFactor(): Double = {
    return ParameterData.getCoderackBehaviorReductionFactor()
  }

  def isSleeper(): Boolean = {
    return ParameterData.getCoderackBehaviorSleeper()
  }

  def getSleepTime(): Long = {
    return ParameterData.getCoderackBehaviorSleepTime()
  }

  def setExecuteFactor(execFactor: Int) {
    ParameterData.setCoderackBehaviorExecuteFactor(execFactor)
  }

  override def processAlgorithm() {
    while (hasMoreCodeletsToProcess()) {
      getCodeletToProcess() match {
        case Some(codeletToProcess) => process(codeletToProcess)
        case None                   => //do nothing
      }
    }
    val execFactor = getExecuteFactor()

    for (count <- 0 until execFactor) {
      coderack ! CoderackActor.Pop
    }
  }

}
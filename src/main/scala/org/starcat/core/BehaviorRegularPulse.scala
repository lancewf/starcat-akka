package org.starcat.core

import org.starcat.util.CircularQueue
import org.starcat.codelets.Codelet
import org.starcat.slipnet.Slipnet
import org.starcat.workspace.Workspace
import org.starcat.coderack.Coderack
import org.starcat.configuration.ParameterData

class BehaviorRegularPulse(queue: CircularQueue[Codelet], component: Component)
  extends RegularPulse(queue, component) {

  def isAdaptiveExecute(): Boolean = {
    return ParameterData.getBehaviorAdaptiveExecute(component)
  }

  def getExecuteFactor(): Int = {
    return ParameterData.getBehaviorExecuteFactor(component)
  }

  def getReductionFactor(): Double = {
    return ParameterData.getBehaviorReductionFactor(component)
  }

  def isSleeper(): Boolean = {
    return ParameterData.getBehaviorSleeper(component)
  }

  def getSleepTime(): Long = {
    return ParameterData.getBehaviorSleepTime(component)
  }

  def setExecuteFactor(execFactor: Int) {
    ParameterData.setBehaviorExecuteFactor(component, execFactor);
  }

  override def processAlgorithm() {
    // This will take codelets from the buffer (the number is determined
    // by slipnet exec factor) and executes those codelets in the
    // slipnet

    component match {
      case slipent: Slipnet => {
        while (!checkIfDoneProcessing()) {
          if (this.hasMoreCodeletsToProcess()) {
            getCodeletToProcess() match {
              case Some(codeletToProcess) => process(codeletToProcess)
              case None                   => //do nothing
            }
          }
        }
      }
      case workspace: Workspace => {
        while (!checkIfDoneProcessing()) {
          if (this.hasMoreCodeletsToProcess()) {
            getCodeletToProcess() match {
              case Some(codeletToProcess) => process(codeletToProcess)
              case None                   => //do nothing
            }
          }
        }
      }
      case coderack: Coderack => {
        while (this.hasMoreCodeletsToProcess()) {
          getCodeletToProcess() match {
            case Some(codeletToProcess) => process(codeletToProcess)
            case None                   => //do nothing
          }
        }
        val execFactor = getExecuteFactor()

        for (count <- 0 until execFactor) {
          coderack.pop()
        }
      }
      case _ => {
        System.out.println("Did not determine a control"
          + " codelet in ControlRegularPulse");
      }
    }
    if (isSleeper() && getIsAlive()) {
      try {
        val time = getSleepTime()
        var count = 0
        while (time > count && getIsAlive()) {
          Thread.sleep(1)
          count += 1
        }
        //Thread.sleep(time);
      } catch {
        case ex: IllegalThreadStateException => {
          return
        }
        case ex: InterruptedException => {
          return
        }
      }
    } else {
      // make thread stop to give other threads a chance to execute
      try {
        Thread.sleep(1)
      } catch {
        case ie: InterruptedException => {
          ie.printStackTrace()
        }
      }
    }
  }

}
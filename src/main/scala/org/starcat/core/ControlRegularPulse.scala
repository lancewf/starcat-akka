package org.starcat.core

import org.starcat.configuration.ParameterData
import org.starcat.util.CircularQueue
import org.starcat.codelets.Codelet
import org.starcat.slipnet.Slipnet
import org.starcat.workspace.Workspace
import org.starcat.coderack.Coderack
import org.starcat.slipnet.SlipnetUpdateCodelet
import org.starcat.workspace.WorkspaceUpdateCodelet
import org.starcat.coderack.CoderackUpdateCodelet

class ControlRegularPulse(queue:CircularQueue[Codelet], component:Component) extends RegularPulse(queue, component) {
  
  def isAdaptiveExecute():Boolean = {
    return ParameterData.getControlAdaptiveExecute(component)
  }
  def getExecuteFactor():Int = {
    return ParameterData.getControlExecuteFactor(component)
  }
  def getReductionFactor():Double = {
    return ParameterData.getControlReductionFactor(component)
  }
  def isSleeper():Boolean = {
    return ParameterData.getControlSleeper(component);
  }
  def getSleepTime():Long =  {
    return ParameterData.getControlSleepTime(component);
  }
  
  def setExecuteFactor(execFactor:Int) {
    ParameterData.setControlExecuteFactor(component, execFactor)
  }

  override def preProcess()
  {
    val executes = getExecuteFactor();
  
    for (i <- 0 until executes)
    {
      component match{
        case slipnet:Slipnet =>{
          val codelet = new SlipnetUpdateCodelet();
          push(codelet);
        }
        case workspace:Workspace =>{
          val codelet = new WorkspaceUpdateCodelet();
        push(codelet);
        }
        case coderack:Coderack =>{
          val codelet = new CoderackUpdateCodelet();
        push(codelet);
        }
        case _ =>{
          System.out.println("Did not determine a control codelet in ControlRegularPulse")
        }
      }
    }
    super.preProcess();
  } 
}
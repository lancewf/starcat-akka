package org.starcat.core

import org.starcat.util.CircularQueue
import org.starcat.codelets.Codelet
import org.starcat.codelets.BehaviorCodelet
import org.starcat.codelets.ControlCodelet
import org.starcat.codelets.CodeletEventListener
import org.starcat.codelets.CodeletEvent

/** TODO this comment does not make sense...
 * This class probably needs an interface to conform to.  Right now it is 
 * just an abstract class.  Essentially, the 
 * <code>StandardMetabolism</code> class provides a strategy for 
 * <em>how</em> a <code>Metabolism</code> runs.  In this implementation, 
 * there are two queues, which separately hold <code>BehaviorCodelet</code> and 
 * <code>ControlCodelet</code> objects.  Each queue has a separate 
 * <code>MetabolismPulse</code> associated with it, and this pulse is 
 * selected to run based on which thread is currently acting when the 
 * <code>pulse()</code> method is called.  I'm sure there are plenty of 
 * ways to set up a strategy to run, that is why it is decoupled from the 
 * <code>MetabolismPulse</code>.  
 */
class StandardMetabolism(component:Component)
            extends CodeletEventListener with Metabolism
{
  // -------------------------------------------------------------------------
    // Protected Data
  // -------------------------------------------------------------------------
    
  protected val behaviorQueue = new CircularQueue[Codelet]()
  protected val controlQueue = new CircularQueue[Codelet]()
  protected val behaviorPulse = new BehaviorRegularPulse(behaviorQueue, component)
  protected val controlPulse = new ControlRegularPulse(controlQueue, component)
  
  // -------------------------------------------------------------------------
    // Public Members
  // -------------------------------------------------------------------------
  
  def handleCodeletEvent(event:CodeletEvent){
    event.codelet match{
      case behaviorCodelet:BehaviorCodelet =>{
        behaviorQueue.push(behaviorCodelet)
      }
      case controlCodelet:ControlCodelet =>{
        controlQueue.push(controlCodelet)
      }
      case _ => //do nothing
    }
  }
  
  def start() {
    behaviorPulse.start()
    controlPulse.start()
  }
  
  def stop() {
    behaviorPulse.stop()
    controlPulse.stop()
  }
  
  def getBehaviorPulse():RegularPulse = behaviorPulse
  
  def getControlPulse():RegularPulse = controlPulse
  
  def getComponent():Component = component
}
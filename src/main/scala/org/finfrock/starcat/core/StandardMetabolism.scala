package org.finfrock.starcat.core

import org.finfrock.starcat.util.CircularQueue
import org.finfrock.starcat.codelets.CodeletEventListener
import org.finfrock.starcat.codelets.CodeletEvent
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props

object StandardMetabolism{
  def getProps(component: ActorRef, behaviorPulse: ActorRef, controlPulse: ActorRef):Props ={
    Props(classOf[StandardMetabolism], component, behaviorPulse, controlPulse)
  }
}
/**
 * TODO this comment does not make sense...
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
class StandardMetabolism(val component: ActorRef,
                         val behaviorPulse: ActorRef,
                         val controlPulse: ActorRef) extends Actor with ActorLogging 
                         with CodeletEventListener with Metabolism {

  def receive = {
    case Metabolism.Start => start()
    case Metabolism.Stop => stop()
    case CodeletEventListener.HandleCodeletEvent(event) => {
//      log.info("handleCodeletEvent at " + self.path.name + " from " + sender.path.name)
      handleCodeletEvent(event)
    }
  }
  
  def handleCodeletEvent(event: CodeletEvent) {
    
    event.codelet.codeletType match {
      case Codelet.BEHAVIOR_CODELET_TYPE => {
        behaviorPulse ! RegularPulseActor.Push(event.codelet)
      }
      case Codelet.CONTROL_CODELET_TYPE => {
        controlPulse ! RegularPulseActor.Push(event.codelet)
      }
      case _ => //do nothing
    }
  }

  def start() {
    behaviorPulse ! RegularPulseActor.Start
    controlPulse ! RegularPulseActor.Start
  }

  def stop() {
    behaviorPulse ! RegularPulseActor.Stop
    controlPulse ! RegularPulseActor.Stop
  }
}
package org.finfrock.starcat.core

import org.finfrock.starcat.codelets.CodeletEventListener
import org.finfrock.starcat.codelets.CodeletEvent
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Actor
import akka.actor.ActorLogging

object Component{
  case class HandleCodeletEvent(event:CodeletEvent)
  case class PreExecuteCodelet(codelet:Codelet)
  case class ExecuteCodelet(codelet:Codelet)
  case class PostExecuteCodelet(codelet:Codelet)
  case class SetMetabolism(metab: ActorRef)
  case class AddCodeletEventListener(listener:ActorRef)
  object Update
}

trait Component extends Actor with ActorLogging {
  
  // -------------------------------------------------------------------------
  // Private Data
  // -------------------------------------------------------------------------

  private var listenerList = List[ActorRef]()
  /*
   * The current codelet being processed, NOT any codelets going out.
   */
  private var metabolismOption:Option[ActorRef] = None

  def receive = {
    case Component.HandleCodeletEvent(event) => handleCodeletEvent(event)
    case Component.PreExecuteCodelet(codelet) => preExecuteCodelet(codelet)
    case Component.ExecuteCodelet(codelet) => executeCodelet(codelet)
    case Component.PostExecuteCodelet(codelet) => postExecuteCodelet(codelet)
    case Component.Update => update()
    case Metabolism.Start => start()
    case Metabolism.Stop => stop()
    case Component.SetMetabolism(metab) => setMetabolism(metab)
    case Component.AddCodeletEventListener(listener) => addCodeletEventListener(listener)
  }
  
  // --------------------------------------------------------------------------
  // CodeletEventListener Members
  // --------------------------------------------------------------------------

  /*
   * Metabolism objects actually do the work of handling events between
   * components, so events are forwarded to them. If a component's metabolism
   * is not set, then the default behavior of the component is to immediately
   * execute the codelet.
   */
  protected def handleCodeletEvent(event:CodeletEvent) {
     metabolismOption match{
      case Some(metabolism) =>{
        metabolism ! CodeletEventListener.HandleCodeletEvent(event)
      }
      case None =>{
        executeCodelet(event.codelet)
      }
    }
  }

  // --------------------------------------------------------------------------
  // Abstract Members
  // --------------------------------------------------------------------------

  protected def preExecuteCodelet(codelet:Codelet)

  protected def executeCodelet(codelet:Codelet)

  protected def postExecuteCodelet(codelet:Codelet)

  protected def update()

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  def setMetabolism(metab: ActorRef) {metabolismOption = Some(metab) }

  def getMetabolism():Option[ActorRef] = metabolismOption

  /*
   * Metabolisms provide the activity for all components. This method simply
   * calls metabolism.start(). If this component has no metabolism (null),
   * then the method does nothing.
   */
  def start() {
     metabolismOption match{
      case Some(metabolism) =>{
        metabolism ! Metabolism.Start
      }
      case None => // do nothing
    }
  }

  /*
   * Metabolisms provide the activity for all components. This method simply
   * calls metabolism.stop(). If the metabolism is null, then this method does
   * nothing.
   */
  def stop() {
     metabolismOption match{
      case Some(metabolism) =>{
        metabolism ! Metabolism.Stop
      }
      case None => // do nothing
    }
  }

  /*
   * Components have listeners to listen for CodeletEvent objects. This is one
   * of the fundamental architectural commitments of Starcat. That is to say:
   * Starcat is an asynchronous message passing architecture, which is quite
   * different from how Copycat's components interacted.
   */
  def addCodeletEventListener(listener:ActorRef) {
    listenerList = listener :: listenerList
  }

  def removeCodeletEventListener(listener:ActorRef) {
    listenerList = listenerList.filter(_ == listener)
  }

  /*
   * NOTE: right now listener lists have a "type" class stored first and the
   * actual component stored second for those situations where we someday
   * might want different kinds of listeners (this happens in the AgentCat
   * code). But we think for now that we will only want CodeletEventListener
   * objects in there This code could be much cleaner using typing less
   * loosely (putting stuff in a list of objects is not so clean)
   * 
   * @param codelet
   */
  def fireCodeletEvent(codelet: Codelet) {
    for { listener <- listenerList } {
      listener ! Component.HandleCodeletEvent(new CodeletEvent(self, codelet))
    }
  }
}
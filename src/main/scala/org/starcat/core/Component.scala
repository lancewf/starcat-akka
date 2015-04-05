package org.starcat.core

import org.starcat.codelets.Codelet
import org.starcat.codelets.CodeletEventListener
import org.starcat.codelets.CodeletEvent

abstract class Component() extends CodeletEventListener {

  private var listenerList = List[CodeletEventListener]()
  
  // -------------------------------------------------------------------------
  // Private Data
  // -------------------------------------------------------------------------

  
  /*
   * The current codelet being processed, NOT any codelets going out.
   */
  private var currentCodeletOption:Option[Codelet] = None
  private var metabolismOption:Option[Metabolism] = None
  private var codeletEvent:CodeletEvent = null

  // --------------------------------------------------------------------------
  // CodeletEventListener Members
  // --------------------------------------------------------------------------

  /*
   * Metabolism objects actually do the work of handling events between
   * components, so events are forwarded to them. If a component's metabolism
   * is not set, then the default behavior of the component is to immediately
   * execute the codelet.
   */
  def handleCodeletEvent(event:CodeletEvent) {
     metabolismOption match{
      case Some(metabolism) =>{
        metabolism.handleCodeletEvent(event)
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

  def executeCodelet(codelet:Codelet)

  protected def postExecuteCodelet(codelet:Codelet)

  def update()

  // --------------------------------------------------------------------------
  // Protected Members
  // --------------------------------------------------------------------------

  protected def getCurrentCodelet():Option[Codelet] = currentCodeletOption

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  def setMetabolism(metab: Metabolism) {metabolismOption = Some(metab) }

  def getMetabolism():Option[Metabolism] = metabolismOption

  /*
   * Metabolisms provide the activity for all components. This method simply
   * calls metabolism.start(). If this component has no metabolism (null),
   * then the method does nothing.
   */
  def start() {
     metabolismOption match{
      case Some(metabolism) =>{
        metabolism.start()
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
        metabolism.stop()
      }
      case None => // do nothing
    }
  }

  /**
   * Codelets executing in a component are registered with the component for
   * the duration of execution. This may or may not be necessary so default
   * implementation is defined here, which essentially sets the currentCodelet
   * field to the argument.
   */
  def registerCodelet(codelet:Codelet) { currentCodeletOption = Some(codelet) }

  /*
   * This method usually just sets the <code>currentCodelet</code> field to
   * null but we pass the codelet in case more needs to be done in some
   * implementations
   */
  def unregisterCodelet(codelet:Codelet) { currentCodeletOption = None }

  /*
   * Components have listeners to listen for CodeletEvent objects. This is one
   * of the fundamental architectural commitments of Starcat. That is to say:
   * Starcat is an asynchronous message passing architecture, which is quite
   * different from how Copycat's components interacted.
   */
  def addCodeletEventListener(listener:CodeletEventListener) {
    listenerList = listener :: listenerList
  }

  def removeCodeletEventListener(listener:CodeletEventListener) {
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
  def fireCodeletEvent(codelet:Codelet) {
    if (codelet != null) {
      codeletEvent = new CodeletEvent(this, codelet);
      
      for{listener <- listenerList}{
        listener.handleCodeletEvent(codeletEvent)
      }
    }
  }
}
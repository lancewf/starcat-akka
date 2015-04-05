package org.starcat.core

import org.starcat.codelets.Codelet
import org.starcat.util.CircularQueue

abstract class RegularPulse(queue:CircularQueue[Codelet], val component:Component) 
  extends MetabolismPulse {

  // -------------------------------------------------------------------------
  // Protected Data
  // -------------------------------------------------------------------------

  private var isAlive = false
  private var beforePulse = 0
  private var concurrent = false
  private var done = false;
  private var executionThread:Thread = new Thread(this)

  /*
   * how many codelets have been executed so far in this pulse(). This should
   * be incremented every time a Codelet is processed.
   */
  private var currentCodeletsExecuted:Int = 0

  // -------------------------------------------------------------------------
  // Abstract Members
  // -------------------------------------------------------------------------

  /**
   * It is rare that an item from the ParameterData class needs both get and
   * set methods. Because executeFactor changes during adaptation (if on) this
   * value needs to be written back to that class. Also, once the run-time GUI
   * for changing those data items is in place, it will be possible to
   * interfere with the adaptation process. So do this at peril.
   * 
   */
  def isAdaptiveExecute():Boolean

  def getExecuteFactor():Int

  def setExecuteFactor(execFactor:Int)

  def getReductionFactor():Double

  def isSleeper():Boolean

  def getSleepTime():Long

  // -------------------------------------------------------------------------
  // MetabolismPulse Members
  // -------------------------------------------------------------------------

  def run() {
    try {
      do {
        algorithmShell()
        Thread.sleep(1)
      } while (isAlive)
    } catch {
      case ie:InterruptedException =>{
      isAlive = false;
      return;
      }
      case ex:IllegalThreadStateException =>{
       isAlive = false;
      return;
      }
      case ex:Exception =>{
      isAlive = false;
      return;
      }
    }
  }

  def start() {
    isAlive = true;
    executionThread.start();
  }

  def stop() {
    isAlive = false;
  }
  
  // -------------------------------------------------------------------------
  // Public Members
  // -------------------------------------------------------------------------
  
  protected def push(codelet:Codelet){
    queue.push(codelet)
  }
  
  /**
   * The algorithm shell for pulsing wraps the main algorithm with concurrency
   * related code. Unless there are bugs or other reasons to make this method
   * overridable, it should stay final.
   */
  def algorithmShell() {
    preProcess();
    processAlgorithm();
    postProcess();
  }

  /**
   * return true if the number of codelets it has executed in this processing
   * cycle is equal to the limit of how many it is allowed to process defined
   * by executeFactor or if there no more codelets available to be processed.
   * Note that if the limit is reached, the codelet count to be processed is
   * reset, but if the limit has not been reached, this number is not reset
   * regardless of whether there are any codelets available to be processed.
   */
  def checkIfDoneProcessing():Boolean = {
    val execFactor = getExecuteFactor();
    if (execFactor > 0) {
      if (currentCodeletsExecuted == execFactor) {
        currentCodeletsExecuted = 0
      }
      (currentCodeletsExecuted == execFactor) || !hasMoreCodeletsToProcess()
    } else {
      !hasMoreCodeletsToProcess()
    }
  }

  def getCodeletToProcess():Option[Codelet] = {
    queue.pop()
  }

  def getCurrentCodeletsExecuted():Int = currentCodeletsExecuted

  /**
   * return <code>true</code> if the queue is not empty, false otherwise
   */
  def hasMoreCodeletsToProcess():Boolean = {
    if (!queue.isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * TODO should we keep this stuff in here? Is that some variant to the arch
   * we can build in?
   * 
   * return true if this Pulse object uses <code>StarcatController</code>,
   * false otherwise
   */
  def isConcurrent():Boolean = concurrent

  /**
   * adjusts the execution factor for the pulse by increasing it or decreasing
   * it based on the algorithm defined in this method
   */
  def postProcess() {
    var execFactor = getExecuteFactor()
    if (isAdaptiveExecute()) {
      val afterPulse = queue.size();
      val change = Math.abs(beforePulse - afterPulse);
      if (afterPulse == 0 && execFactor > 1) {
        execFactor = execFactor - Math.round(execFactor * getReductionFactor()).toInt
      } else {
        execFactor += change;
      }
      setExecuteFactor(execFactor);
    }
  }

  /**
   * for adaptation purposes, the pulse needs to know how big the queue is
   * before processing, which is what this implementation of the method does
   */
  def preProcess() {
    beforePulse = queue.size()
  }

  /**
   * increments the "codelets processed" count and executes the codelet.
   */
  def process(codelet:Codelet) {
      currentCodeletsExecuted+=1
      component.executeCodelet(codelet)
  }

  /**
   * the algorithm here is essentially:
   * 
   * if (not done processing) if (have more codelets to do something with) do
   * something with them if (this pulse sleeps after finishing codelet
   * processing) sleep
   * 
   */
  def processAlgorithm() {
    while (!checkIfDoneProcessing()) {
      // removed if statement to check for more codelets to process
      getCodeletToProcess() match{
        case Some(codeletToProcess) =>process(codeletToProcess)
        case None => //do nothing
      }
    }
    if (isSleeper() && isAlive) {
      try {
        val time = getSleepTime()
        var count = 0L
        while (time > count && isAlive) {
          Thread.sleep(1)
          count+=1
        }
        // Thread.sleep(getSleepTime());
      } catch {
        case ie: InterruptedException =>{
          ie.printStackTrace()
        }
        case ex: Exception =>{
          return 
        }
      }
    }
  }

  def getIsAlive():Boolean = isAlive

  def setConcurrent(concurrent:Boolean) {
    this.concurrent = concurrent;
  }

  def setDone(done:Boolean) {
    this.done = done
  }

  def isDone():Boolean = done
}
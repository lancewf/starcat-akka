package org.finfrock.starcat.core

import org.finfrock.starcat.util.CircularQueue
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Actor
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import akka.actor.ActorLogging
import akka.actor.Cancellable

object RegularPulseActor{
  object Run
  object Start
  object Stop
  case class Push(codelet:Codelet)
}

abstract class RegularPulseActor(val component: ActorRef)
  extends Actor with ActorLogging  {

  // -------------------------------------------------------------------------
  // Protected Data
  // -------------------------------------------------------------------------

  private var isAlive = false
  private var beforePulse = 0
  private var done = false;
  private val queue = new CircularQueue[Codelet]()
  private var cancellableOption:Option[Cancellable] = None
  implicit val ex = this.context.dispatcher

  /*
   * how many codelets have been executed so far in this pulse(). This should
   * be incremented every time a Codelet is processed.
   */
  private var currentCodeletsExecuted: Int = 0

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
  def isAdaptiveExecute(): Boolean

  def getExecuteFactor(): Int

  def setExecuteFactor(execFactor: Int)

  def getReductionFactor(): Double

  def isSleeper(): Boolean

  def getSleepTime(): Long

  def receive = {
    case RegularPulseActor.Run => {
      cancellableOption match{
        case Some(cancellable) => {
          cancellable.cancel()
          cancellableOption = None
        }
        case None => // do nothing
      }
      
      if(isAlive){
        algorithmShell()
        cancellableOption = Some(context.system.scheduler.scheduleOnce(
            FiniteDuration(getSleepTime(), java.util.concurrent.TimeUnit.MILLISECONDS), 
            self, RegularPulseActor.Run))
      }
    }
    case RegularPulseActor.Start =>{
      log.info("Starting: " + self.path.name)
      isAlive = true
      self ! RegularPulseActor.Run
    }
    case RegularPulseActor.Stop =>{
      log.info("Stopping: " + self.path.name)
      isAlive = false
    }
    case RegularPulseActor.Push(codelet:Codelet) =>{
//      log.info("Pushing Codelet: " + codelet.name + " from: " + sender.path)
      push(codelet)
    }
  }

  // -------------------------------------------------------------------------
  // Public Members
  // -------------------------------------------------------------------------

  protected def push(codelet: Codelet) {
    queue.push(codelet)
  }

  /**
   * The algorithm shell for pulsing wraps the main algorithm with concurrency
   * related code. Unless there are bugs or other reasons to make this method
   * overridable, it should stay final.
   */
  private def algorithmShell() {
    preProcess()
    processAlgorithm()
    postProcess()
  }

  /**
   * return true if the number of codelets it has executed in this processing
   * cycle is equal to the limit of how many it is allowed to process defined
   * by executeFactor or if there no more codelets available to be processed.
   * Note that if the limit is reached, the codelet count to be processed is
   * reset, but if the limit has not been reached, this number is not reset
   * regardless of whether there are any codelets available to be processed.
   */
  protected def checkIfDoneProcessing(): Boolean = {
    val execFactor = getExecuteFactor()
//    log.info("execFactor: " + execFactor)
    if (execFactor > 0) {
      if (currentCodeletsExecuted == execFactor) {
        currentCodeletsExecuted = 0
        true
      } else{
//        log.info("hasMoreCodeletsToProcess: " + hasMoreCodeletsToProcess())
//        log.info("queue.isEmpty(): " + queue.isEmpty())
        !hasMoreCodeletsToProcess()
      }
    } else {
      !hasMoreCodeletsToProcess()
    }
  }

  protected def getCodeletToProcess(): Option[Codelet] = queue.pop()

  private def getCurrentCodeletsExecuted(): Int = currentCodeletsExecuted

  protected def hasMoreCodeletsToProcess(): Boolean = !queue.isEmpty()
  
  protected def numberOfCodelets = queue.size()

  /**
   * adjusts the execution factor for the pulse by increasing it or decreasing
   * it based on the algorithm defined in this method
   */
  private def postProcess() {
    var execFactor = getExecuteFactor()
    if (isAdaptiveExecute()) {
      val afterPulse = queue.size()
      val change = Math.abs(beforePulse - afterPulse)
      if (afterPulse == 0 && execFactor > 1) {
        execFactor = execFactor - Math.round(execFactor * getReductionFactor()).toInt
      } else {
        execFactor += change
      }
      setExecuteFactor(execFactor)
    }
  }

  /**
   * for adaptation purposes, the pulse needs to know how big the queue is
   * before processing, which is what this implementation of the method does
   */
  protected def preProcess() {
    beforePulse = queue.size()
//    log.info("beforePulse: " + beforePulse)
  }

  /**
   * increments the "codelets processed" count and executes the codelet.
   */
  protected def process(codelet: Codelet) {
    currentCodeletsExecuted += 1
    component ! Component.ExecuteCodelet(codelet)
  }

  /**
   * the algorithm here is essentially:
   *
   * if (not done processing) if (have more codelets to do something with) do
   * something with them if (this pulse sleeps after finishing codelet
   * processing) sleep
   *
   */
  protected def processAlgorithm() {
    while (!checkIfDoneProcessing()) {
      getCodeletToProcess() match {
        case Some(codeletToProcess) => process(codeletToProcess)
        case None                   => //do nothing
      }
    }
  }

  private def getIsAlive(): Boolean = isAlive

  private def setDone(done: Boolean) {
    this.done = done
  }

  private def isDone(): Boolean = done
}
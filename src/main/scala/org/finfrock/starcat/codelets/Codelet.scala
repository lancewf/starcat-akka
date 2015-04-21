package org.finfrock.starcat.codelets

import org.joda.time.DateTime
import akka.actor.ActorRef
import scala.concurrent.Future
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration._
import akka.util.Timeout

object Codelet{
  val BEHAVIOR_CODELET_TYPE = "BehaviorCodeletActor"
  val CONTROL_CODELET_TYPE = "ControlCodelet"
}

case class Codelet(val name:String, val codeletActor:ActorRef, val urgency:Double = 0, 
    val numberToEmit:Int = 0, val codeletType:String = Codelet.BEHAVIOR_CODELET_TYPE, 
    val timeToDie:DateTime = DateTime.now().plusYears(1)) {
  
  // -----------------------------------------------------------------------------
  // Abstract Members
  // -----------------------------------------------------------------------------
  
  def preExecuteCoderack(coderack: ActorRef){
    codeletActor ! CodeletActor.PreExecuteCoderack(coderack)
  }

  def preExecuteSlipnet(slipnet: ActorRef){
    codeletActor ! CodeletActor.PreExecuteSlipnet(slipnet)
  }

  def preExecuteWorkspace(workspace: ActorRef){
    codeletActor ! CodeletActor.PreExecuteWorkspace(workspace)
  }

  def executeCoderack(coderack: ActorRef){
    codeletActor ! CodeletActor.ExecuteCoderack(coderack)
  }

  def executeSlipnet(slipnet: ActorRef){
    codeletActor ! CodeletActor.ExecuteSlipnet(slipnet)
  }

  def executeWorkspace(workspace: ActorRef){
    codeletActor ! CodeletActor.ExecuteWorkspace(workspace)
  }

  def postExecuteCoderack(coderack: ActorRef){
    codeletActor ! CodeletActor.PostExecuteCoderack(coderack)
  }

  def postExecuteSlipnet(slipnet: ActorRef){
    codeletActor ! CodeletActor.PostExecuteSlipnet(slipnet)
  }

  def postExecuteWorkspace(workspace: ActorRef){
    codeletActor ! CodeletActor.PostExecuteWorkspace(workspace)
  }
}
package org.finfrock.starcat.codelets

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import org.joda.time.DateTime

object CodeletActor {
  case class PreExecuteCoderack(coderack: ActorRef)
  case class PreExecuteSlipnet(slipnet: ActorRef)
  case class PreExecuteWorkspace(workspace: ActorRef)

  case class ExecuteCoderack(coderack: ActorRef)
  case class ExecuteSlipnet(slipnet: ActorRef)
  case class ExecuteWorkspace(workspace: ActorRef)

  case class PostExecuteCoderack(coderack: ActorRef)
  case class PostExecuteSlipnet(slipnet: ActorRef)
  case class PostExecuteWorkspace(workspace: ActorRef)
  object GetTimeToDie
  object GetClonedCodelet
}
trait CodeletActor extends Actor with ActorLogging {
  def receive = {
    case CodeletActor.PreExecuteCoderack(coderack)    => preformPreExecuteCoderack(coderack)
    case CodeletActor.PreExecuteSlipnet(slipnet)      => preformPreExecuteSlipnet(slipnet)
    case CodeletActor.PreExecuteWorkspace(workspace)  => preformPreExecuteWorkspace(workspace)
    case CodeletActor.ExecuteCoderack(coderack)       => preformExecuteCoderack(coderack)
    case CodeletActor.ExecuteSlipnet(slipnet)         => preformExecuteSlipnet(slipnet)
    case CodeletActor.ExecuteWorkspace(workspace)     => preformExecuteWorkspace(workspace)
    case CodeletActor.PostExecuteCoderack(coderack)   => preformPostExecuteCoderack(coderack)
    case CodeletActor.PostExecuteSlipnet(slipnet)     => preformPostExecuteSlipnet(slipnet)
    case CodeletActor.PostExecuteWorkspace(workspace) => preformPostExecuteWorkspace(workspace)
    case CodeletActor.GetClonedCodelet                => sender ! cloneCodelet()
  }
  
  protected def cloneCodelet():ActorRef

  protected def preformPreExecuteCoderack(coderack: ActorRef)

  protected def preformPreExecuteSlipnet(slipnet: ActorRef)

  protected def preformPreExecuteWorkspace(workspace: ActorRef)

  protected def preformExecuteCoderack(coderack: ActorRef)

  protected def preformExecuteSlipnet(slipnet: ActorRef)

  protected def preformExecuteWorkspace(workspace: ActorRef)

  protected def preformPostExecuteCoderack(coderack: ActorRef)

  protected def preformPostExecuteSlipnet(slipnet: ActorRef)

  protected def preformPostExecuteWorkspace(workspace: ActorRef)
}
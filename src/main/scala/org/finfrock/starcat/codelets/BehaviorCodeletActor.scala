package org.finfrock.starcat.codelets

import akka.actor.ActorRef
import org.finfrock.starcat.slipnet.SlipnetNodeActor
import org.finfrock.starcat.coderack.CoderackActor

object BehaviorCodeletActor{
  case class WorkspaceSuccess(successful:Boolean)
}

trait BehaviorCodeletActor extends CodeletActor {
  private var workspaceSuccess = false
  val successActivationRecipients:List[SlipnetNodeActorActivationRecipient]
  val failureActivationRecipients:List[SlipnetNodeActorActivationRecipient]
  
  override def receive = super.receive orElse {
    case BehaviorCodeletActor.WorkspaceSuccess(successful) => workspaceSuccess = successful
  }

  protected def preformPreExecuteCoderack(coderack: ActorRef){}

  protected def preformPreExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPreExecuteWorkspace(workspace: ActorRef){}

  protected def preformExecuteCoderack(coderack: ActorRef){
  }
  
  protected def preformExecuteSlipnet(slipnet: ActorRef){
    if (workspaceSuccess) {
      for (successfullRecipient <- successActivationRecipients) {
        successfullRecipient.activationSlipnetNodeRecipient ! 
          SlipnetNodeActor.AddActivationToBuffer(successfullRecipient.amountToAdd)
      }
    } else {
      for (failureRecipient <- failureActivationRecipients) {
        failureRecipient.activationSlipnetNodeRecipient ! 
          SlipnetNodeActor.AddActivationToBuffer(failureRecipient.amountToAdd)
      }
    }
  }

  protected def preformExecuteWorkspace(workspace: ActorRef){}

  protected def preformPostExecuteCoderack(coderack: ActorRef){}

  protected def preformPostExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPostExecuteWorkspace(workspace: ActorRef){}
  
}
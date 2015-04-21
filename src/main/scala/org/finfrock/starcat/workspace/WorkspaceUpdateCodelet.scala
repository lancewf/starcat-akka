package org.finfrock.starcat.workspace

import akka.actor.ActorRef
import org.finfrock.starcat.codelets.CodeletActor
import akka.actor.Props
import org.finfrock.starcat.core.Component

object WorkspaceUpdateCodelet{
  def getProps():Props = Props(classOf[WorkspaceUpdateCodelet])
}

class WorkspaceUpdateCodelet extends CodeletActor {
  
  def cloneCodelet(): ActorRef = {
    context.system.actorOf(WorkspaceUpdateCodelet.getProps())
  }
  
  protected def preformPreExecuteCoderack(coderack: ActorRef){}

  protected def preformPreExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPreExecuteWorkspace(workspace: ActorRef){}

  protected def preformExecuteCoderack(coderack: ActorRef){}

  protected def preformExecuteSlipnet(slipnet: ActorRef){}

  protected def preformExecuteWorkspace(workspace: ActorRef){
    workspace ! Component.Update
  }

  protected def preformPostExecuteCoderack(coderack: ActorRef){}

  protected def preformPostExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPostExecuteWorkspace(workspace: ActorRef){}
}
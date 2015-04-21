package org.finfrock.starcat.coderack

import akka.actor.ActorRef
import org.finfrock.starcat.codelets.CodeletActor
import org.finfrock.starcat.core.Component
import akka.actor.Props

object CoderackUpdateCodelet{
  def getProps():Props = Props(classOf[CoderackUpdateCodelet])
}

class CoderackUpdateCodelet extends CodeletActor {
  
  def cloneCodelet(): ActorRef = {
    context.system.actorOf(CoderackUpdateCodelet.getProps())
  }
  
  protected def preformPreExecuteCoderack(coderack: ActorRef){}

  protected def preformPreExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPreExecuteWorkspace(workspace: ActorRef){}

  protected def preformExecuteCoderack(coderack: ActorRef){
    coderack ! Component.Update
  }

  protected def preformExecuteSlipnet(slipnet: ActorRef){}

  protected def preformExecuteWorkspace(workspace: ActorRef){}

  protected def preformPostExecuteCoderack(coderack: ActorRef){}

  protected def preformPostExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPostExecuteWorkspace(workspace: ActorRef){}
}
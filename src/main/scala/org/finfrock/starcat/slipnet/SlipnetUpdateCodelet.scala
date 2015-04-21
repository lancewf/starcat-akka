package org.finfrock.starcat.slipnet

import akka.actor.ActorRef
import org.finfrock.starcat.codelets.CodeletActor
import org.finfrock.starcat.core.Component
import akka.actor.Props

object SlipnetUpdateCodelet{
  def getProps():Props = Props(classOf[SlipnetUpdateCodelet])
}

class SlipnetUpdateCodelet extends CodeletActor{
  
  def cloneCodelet(): ActorRef = {
    context.system.actorOf(SlipnetUpdateCodelet.getProps())
  }

  protected def preformPreExecuteCoderack(coderack: ActorRef){}

  protected def preformPreExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPreExecuteWorkspace(workspace: ActorRef){}

  protected def preformExecuteCoderack(coderack: ActorRef){}

  protected def preformExecuteSlipnet(slipnet: ActorRef){
    slipnet ! Component.Update
  }

  protected def preformExecuteWorkspace(workspace: ActorRef){}

  protected def preformPostExecuteCoderack(coderack: ActorRef){}

  protected def preformPostExecuteSlipnet(slipnet: ActorRef){}

  protected def preformPostExecuteWorkspace(workspace: ActorRef){}
}
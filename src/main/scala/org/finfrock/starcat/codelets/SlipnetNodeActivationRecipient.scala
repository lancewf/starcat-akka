package org.finfrock.starcat.codelets

import akka.actor.ActorRef

case class SlipnetNodeActorActivationRecipient(activationSlipnetNodeRecipient:ActorRef,
      amountToAdd:Int)
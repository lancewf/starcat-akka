package org.finfrock.starcat.codelets

import org.finfrock.starcat.util.FuzzySet
import akka.actor.ActorRef
import org.finfrock.starcat.slipnet.SlipnetNodeActor
import scala.concurrent.duration._
import akka.util.Timeout

trait FuzzyBehaviorCodeletActor extends BehaviorCodeletActor {
  val successFuzzySet: FuzzySet
  val failureFuzzySet: FuzzySet
  
  private var crispValue: Double = 0.0
  
  protected def setCrispValue(newCrispValue: Double) {
    this.crispValue = newCrispValue
  }
  
  override def receive = super.receive orElse {
    case FuzzyBehaviorCodeletActor.SetCrispValue(newCrispValue) => {
      setCrispValue(newCrispValue)
    }
    case FuzzyBehaviorCodeletActor.GetCrispValue => sender ! crispValue
  }

  override def preformExecuteSlipnet(slipnet: ActorRef) {
    val successMemberValue = successFuzzySet.getMemberValue(crispValue)
    val failureMemberValue = failureFuzzySet.getMemberValue(crispValue)
    for {
      successfullRecipient <- successActivationRecipients
      amountToAdd = (successMemberValue * successfullRecipient.amountToAdd).toInt
      slipnetNodeRecipient = successfullRecipient.activationSlipnetNodeRecipient
    } {
      slipnetNodeRecipient ! SlipnetNodeActor.AddActivationToBuffer(amountToAdd)
    }

    for {
      failureRecipient <- failureActivationRecipients
      amountToAdd = (failureMemberValue *
        failureRecipient.amountToAdd.toDouble).toInt
      slipnetNodeRecipient = failureRecipient.activationSlipnetNodeRecipient
    } {
      slipnetNodeRecipient ! SlipnetNodeActor.AddActivationToBuffer(amountToAdd)
    }
  }
}

object FuzzyBehaviorCodeletActor{
  case class SetCrispValue(crispValue:Double)
  object GetCrispValue
}
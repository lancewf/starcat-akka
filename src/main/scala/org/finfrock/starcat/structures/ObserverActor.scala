package org.finfrock.starcat.structures

import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.ActorLogging

object ObserverActor {
  case class Update(observable: ActorRef, arg: Any)
}
trait ObserverActor extends Actor with ActorLogging{

  def receive = {
    case ObserverActor.Update(observable, arg) => update(observable, arg)
  }
  
  def update(observable: ActorRef, arg: Any)
}
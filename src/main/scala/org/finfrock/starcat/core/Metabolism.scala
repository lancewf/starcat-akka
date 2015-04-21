package org.finfrock.starcat.core

import org.finfrock.starcat.codelets.CodeletEventListener
import akka.actor.ActorRef

object Metabolism{
  object Start
  object Stop
}
trait Metabolism extends CodeletEventListener{
  def start()
  def stop()
  val component:ActorRef
}
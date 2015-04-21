package org.finfrock.starcat.codelets

object CodeletEventListener{
  case class HandleCodeletEvent(event:CodeletEvent)
}
trait CodeletEventListener {
  def handleCodeletEvent(event:CodeletEvent)
}
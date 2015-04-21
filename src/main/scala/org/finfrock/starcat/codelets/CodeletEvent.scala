package org.finfrock.starcat.codelets

import java.util.EventObject
import akka.actor.ActorRef

case class CodeletEvent( source:ActorRef, val codelet:Codelet)
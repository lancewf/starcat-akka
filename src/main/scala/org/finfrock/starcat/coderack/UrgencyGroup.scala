package org.finfrock.starcat.coderack

import scala.util.Random
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.Codelet
import scala.collection.mutable

class UrgencyGroup(val urgency: Double)  {
    private val members = new mutable.ArrayBuffer[Codelet]()
    
    def add(codelet:Codelet) {
        members.append(codelet)
    }
    
    def remove(rng:Random):Codelet = {
        return members.remove(rng.nextInt(members.size))
    }
    
    def remove(removeCodelet:Codelet) {
      members -= removeCodelet
    }
    
    def size() = members.size
}
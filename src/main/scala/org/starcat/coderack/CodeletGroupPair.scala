package org.starcat.coderack

import org.starcat.codelets.Codelet

class CodeletGroupPair(val codelet:Codelet, val group:UrgencyGroup) {
  val sequenceNumber = getSequenceNumber()
  
  private def getSequenceNumber(): Int = {
    if (CodeletGroupPair.runningSequenceNumber < Integer.MAX_VALUE) {
      CodeletGroupPair.runningSequenceNumber += 1
      CodeletGroupPair.runningSequenceNumber
    } else {
      1
    }
  }
}

object CodeletGroupPair{
  var runningSequenceNumber = 1
}
package org.starcat.coderack

import org.starcat.codelets.Codelet
import scala.util.Random

class UrgencyGroup(val urgency: Double)  {
    private var members = new scala.collection.mutable.ArrayBuffer[Codelet]()
    
    // -------------------------------------------------------------------------
    // Public Members
    // -------------------------------------------------------------------------
    
    def add(codelet:Codelet) {
        members.append(codelet)
    }
    
    def remove(rng:Random):Codelet = {
        return members.remove(rng.nextInt(members.size));
    }
    
    /**
     * This is done only when you need to get rid of a dead codelet. So
     * it shouldn't happen very often. However, the operation is O(n) 
     * where n is the list length. If it does happen a lot, we may 
     * need to do something more like a TreeMap instead of an 
     * ArrayList. With a TreeMap, insertion could be more expensive.
     * 
     * @param codelet - codelet to be removed
     */
    def remove(removeCodelet:Codelet) {
      members = members.filterNot(codelet => codelet == removeCodelet)
    }
    
    def size() = members.size
}
package org.starcat.coderack

import java.util.TreeMap
import org.joda.time.DateTime

class LifetimeTable {
// -------------------------------------------------------------------------
    // Private Data
  // -------------------------------------------------------------------------
    
    private val timetable 
            = new TreeMap[CodeletGroupPair, DateTime](new LifetimeComparator())
            
    
    // -------------------------------------------------------------------------
    // Public Members
    // -------------------------------------------------------------------------
    
    def addCodelet(codeletGroupPair:CodeletGroupPair) {
        timetable.put(codeletGroupPair, 
                codeletGroupPair.codelet.getTimeToDie());
    }
    
    def getDeadCodelets(deathDate:DateTime):Array[CodeletGroupPair] = {
        return Array[CodeletGroupPair]()
    }
    
    def removeCodelet(codeletGroupPair:CodeletGroupPair) {
        timetable.remove(codeletGroupPair.codelet.getTimeToDie())
    }
}
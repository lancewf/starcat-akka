package org.starcat.coderack

import java.util.Comparator

class UrgencyComparator extends Comparator[Any]{
    /**
     * Compares two values. Assumes both values are either Double's or
     * UrgencyGroup's. In the latter case, it substitutes the group's 
     * urgency value. Puts bigger values ahead of smaller values.
     *
     * @param o1    Had better be a Double or UrgenncyGroup.
     * @param o2    Had also better be a Double or UrgencyGroup.
     *
     * @return 1 if o1<o2, -1 if o1>o2, 0 otherwise.
     */
    def compare(o1:Any, o2:Any) : Int = {
        val urgency1 = o1 match{
          case double:Double => double
          case urgencyGroup:UrgencyGroup => urgencyGroup.urgency
        }
        
        val urgency2 = o2 match{
          case double:Double => double
          case urgencyGroup:UrgencyGroup => urgencyGroup.urgency
        }
        
        //  Now do the real comparison.
        if (urgency1 < urgency2) 
        {
            return 1;
        } 
        else if (urgency1 > urgency2) 
        {
            return -1;
        } 
        else 
        {
            return 0;
        }
    }
    
    /*
     * Need this method for the Comparator interface.
     *
     * @param o The object to compare to.
     *
     * @return Whether o is exactly this (same address).
     */
    override def equals(o:Any):Boolean = o==this
}
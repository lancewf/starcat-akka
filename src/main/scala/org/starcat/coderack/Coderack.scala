package org.starcat.coderack

import scala.util.Random
import org.starcat.codelets.Codelet
import scala.collection.JavaConversions._
import org.joda.time.DateTime
import org.starcat.core.Component

class Coderack extends Component {
// -------------------------------------------------------------------------
  // Private Data
  // -------------------------------------------------------------------------

  private var totalUrgency:Double = 0.0

  private val groups:java.util.TreeMap[Double, UrgencyGroup] = new java.util.TreeMap[Double, UrgencyGroup](new UrgencyComparator())

  private val lifetimeTable:LifetimeTable = new LifetimeTable();

  private val rng:Random = new Random()

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  /**
   * Adds a codelet to the coderack at the given urgency level.
   * 
   * @param codelet
   *            The Codelet to enqueue.
   * @param urgency
   *            The urgency level to assign to the codelet.
   * 
   * @throws IllegalArgumentException
   *             if the urgency level is non-positive.
   */
  def push(codelet:Codelet, urgency:Double) {
    // Normal housekeeping.
    if (codelet == null) {
      return;
    }
    if (urgency < 0) {
      throw new IllegalArgumentException("Urgency value must be positive");
    }

    // Figure out which group to add to, and if no group for the given
    // urgency value exists, create one.
    val foundGroup = groups.get(urgency)
    
    val group = if (foundGroup == null) {
      makeGroup(urgency)
    } else{
      foundGroup
    }

    // Finally, add the codelet to the appropriate group and
    // update state, making sure to update the lifetime table while
    // you are at it.
    addCodelet(codelet, group)
    lifetimeTable.addCodelet(new CodeletGroupPair(codelet, group))
  }

  def executeCodelet(codelet:Codelet) {
    preExecuteCodelet(codelet)
    codelet.execute(this)
    postExecuteCodelet(codelet)
  }

  def update() {
    // There may be more to do here...
    killDeadCodelets()
  }

  /**
   * Makes a stochastic choice as to which codelet to dequeue from the
   * coderack, and returns it.
   * 
   * @return a Codelet object chosen stochastically, by urgency
   */
  def pop() {
    // Make sure we can actually do this.
    if (groups.size() == 0) {
      return
    }

    // Draw a random number and see just how deep we need
    // to go into the queue.
    val targetValue = rng.nextDouble() * totalUrgency

    // Find the urgency group that we need to draw the codelet from.
    val it:java.util.Iterator[Double] = groups.keySet().iterator()
    var group:UrgencyGroup = null
    var urgencySum = 0.0
    do {
      group = groups.get(it.next())
      urgencySum += (group.size * group.urgency)
    } while (urgencySum < targetValue);

    // Now we've found the group. So remove a codelet from the group,
    // update the state variables, and return the codelet.
    val codelet = removeCodelet(group)

    fireCodeletEvent(codelet)
  }

  // --------------------------------------------------------------------------
  // Protected Members
  // --------------------------------------------------------------------------

  protected def postExecuteCodelet(codelet:Codelet) {
    codelet.postExecute(this);
  }

  protected def preExecuteCodelet(codelet:Codelet) {
    codelet.preExecute(this);
  }

  // --------------------------------------------------------------------------
  // Private Members
  // --------------------------------------------------------------------------

  /**
   * Used to create a new group and add it to the set of urgency groups.
   * 
   * @param urgency
   *            The urgency level associated with the new group.
   * 
   * @return The new group.
   */
  private def makeGroup(urgency:Double):UrgencyGroup = {
    val group = new UrgencyGroup(urgency)
    groups.put(urgency, group)
    group
  }

  /**
   * Removes an urgency group from the set of groups.
   * 
   * @param group
   *            The group to remove.
   */
  private def removeGroup(group:UrgencyGroup) {
    groups.remove(group)
  }

  /**
   * Adds a codelet to the coderack.
   * 
   * @param codelet
   *            The codelet to add.
   * @param group
   *            The urgency group to add it to.
   */
  private def addCodelet(codelet:Codelet, group:UrgencyGroup) {
    group.add(codelet)
    updateUrgency(group.urgency)
  }

  /**
   * Remove a codelet from the specified urgency group.
   * 
   * @param group
   *            The urgency group to remove the codelet from.
   * 
   * @return The codelet that was removed.
   */
  private def removeCodelet(group:UrgencyGroup):Codelet = {
    val codelet = group.remove(rng)
    updateUrgency(0 - group.urgency)

    if (group.size() == 0) {
      removeGroup(group)
    }
    return codelet
  }

  /**
   * Augments the total urgency by the given amount (positive or negative).
   * Currently not instrumented but might become instrumented.
   * 
   * @param urgency
   *            The urgency value to augment.
   */
  def updateUrgency(urgency:Double) {
    totalUrgency += urgency

    // Just in case we need get roundoff error.
    if (totalUrgency < 0.0) {
      totalUrgency = 0.0;
    }
  }

  def killDeadCodelets() {
    val deadGuys = lifetimeTable.getDeadCodelets(DateTime.now())

    for {index <- 0 until deadGuys.length
      deadCodeletGroupPair = deadGuys(index)} {
        
      // First remove the codelet group pair from the lifetime table.
      lifetimeTable.removeCodelet(deadCodeletGroupPair)
      
      // Now remove the codelet from the urgency group.
      deadCodeletGroupPair.group.remove(deadCodeletGroupPair.codelet)
    }
  }
}
package org.starcat.structures

import scala.collection.mutable.HashSet
import scala.util.Random
import java.util.Observable
import org.starcat.codelets.BehaviorCodelet
import org.joda.time.DateTime

abstract class Entity extends Observable with Cloneable {
  private val descriptors = new HashSet[Descriptor]()
  private var relevance: Int = 0
  private var completeness: Int = 0
  private var salience: Int = 0
  private val age: DateTime = DateTime.now

  update()

  // --------------------------------------------------------------------------
  // Object Members
  // --------------------------------------------------------------------------

  override def clone(): Entity = {
    try {
      super.clone().asInstanceOf[Entity]
    } catch {
      case (e: CloneNotSupportedException) => {
        // Auto-generated catch block
        e.printStackTrace();
        throw new InternalError();
      }
    }
  }

  // --------------------------------------------------------------------------
  // Local Protected Members
  // --------------------------------------------------------------------------

  protected def computeRelevance()
  protected def computeCompleteness()

  // This method contains the standard calculation for salience.
  // Children could overloaded if needed.
  protected def computeSalience() {
    val temp_salience = math.round((getRelevance() + (100 - getCompleteness())) / 2.0f);
    salience = temp_salience;
  }

  //Clients will override this if there is additional update behavior
  protected def additionalUpdate() {}

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  def addDescriptor(descriptor: Descriptor) {
    descriptors.add(descriptor)
  }

  def removeDescriptor(descriptor: Descriptor): Boolean = {
    return descriptors.remove(descriptor);
  }

  def getDescriptors(): Set[Descriptor] = {
    return descriptors.toSet
  }

  def descriptorIterator(): Iterator[Descriptor] = {
    return descriptors.toIterator
  }

  def hasDescriptor(des: Descriptor): Boolean = {
    return descriptors.contains(des);
  }

  def getRandomDescriptor(): Descriptor = {
    var toPick = new Random().nextInt() % descriptors.size
    val i = descriptors.toIterator
    while (toPick > 0) {
      i.next();
      toPick -= 1;
    }
    return i.next()
  }

  def equals(anotherEntity: Entity): Boolean = {
    return super.equals(anotherEntity);
  }

  def setChangedAndNotify() {
    setChanged();
    notifyObservers();
  }

  def setChangedAndNotify(arg: Any) {
    setChanged();
    notifyObservers(arg);
  }

  def getRelevance(): Int = {
    return relevance;
  }

  def getCompleteness(): Int = {
    return completeness;
  }

  def update() {
    computeRelevance();
    computeCompleteness();
    computeSalience();
    additionalUpdate();
  }

  def getSalience(): Int = {
    return salience;
  }

  /**
   *  This method provides the workspace update routine to determine a
   *  workspace entity's age to determine if it is old enough to be removed
   *  from the workspace
   * @return
   */
  def getAge(): Long = {
    return DateTime.now().getMillis - age.getMillis;
  }
}
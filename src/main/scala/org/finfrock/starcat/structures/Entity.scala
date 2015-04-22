package org.finfrock.starcat.structures

import scala.collection.mutable.HashSet
import scala.util.Random
import java.util.Observable
import org.joda.time.DateTime
import akka.actor.Actor
import akka.actor.ActorLogging
import java.util.Observer
import akka.actor.ActorRef

object Entity {
  case class AddObserver(observer: ActorRef)
  case class DeleteObserver(observer: ActorRef)
  case class NotifyObservers(obj:Any)
  object DeleteObservers
  object HasChanged
  case class HasChangedResponse(hasChanged:Boolean)
  object CountObservers
  case class CountObserversResponse(countObservers:Int)
  case class AddDescriptor(desc:Descriptor)
  case class RemoveDescriptor(desc:Descriptor)
  object GetDescriptors
  case class GetDescriptorsResponse(desc:Set[Descriptor])
  case class HasDescriptor(des: Descriptor)
  case class HasDescriptorResponse(hasDescriptor:Boolean)
  object GetRandomDescriptor
  case class GetRandomDescriptorResponse(des: Descriptor)
  case class Equals(anotherEntity: Entity)
  case class EqualsResponse(equals:Boolean)
  case class SetChangedAndNotify(arg:Any)
  object GetRelevance
  case class GetRelevanceResponse(relevance:Int)
  object GetCompleteness
  case class GetCompletenessResponse(completeness:Int)
  object Update
  object GetSalience
  case class GetSalienceResponse(salience:Int)
  object GetAge
  case class GetAgeResponse(age:Long)
  object GetEntityType
  case class GetEntityTypeResponse(entityType:String)
}

trait Entity extends Actor with ActorLogging {
  private val descriptors = new HashSet[Descriptor]()
  private var relevance: Int = 0
  private var completeness: Int = 0
  private var salience: Int = 0
  private val age: DateTime = DateTime.now
  private var observers = List[ActorRef]()
  private var changed = false
  
  val entityType:String

  update()
  
  def receive = {
    case Entity.AddObserver(observer: ActorRef) => addObserver(observer)
    case Entity.DeleteObserver(observer: ActorRef) => deleteObserver(observer)
    case Entity.NotifyObservers(obj:Any) => notifyObservers(obj)
    case Entity.DeleteObservers => deleteObservers()
    case Entity.HasChanged => sender ! Entity.HasChangedResponse(hasChanged())
    case Entity.CountObservers => sender ! Entity.CountObserversResponse(countObservers)
    case Entity.AddDescriptor(desc:Descriptor) => addDescriptor(desc)
    case Entity.RemoveDescriptor(desc:Descriptor) => removeDescriptor(desc)
    case Entity.GetDescriptors => sender ! Entity.GetDescriptorsResponse(getDescriptors())
    case Entity.HasDescriptor(des) => sender ! Entity.HasDescriptorResponse(hasDescriptor(des))
    case Entity.GetRandomDescriptor => sender ! Entity.GetRandomDescriptorResponse(getRandomDescriptor())
    case Entity.Equals(anotherEntity: Entity) => Entity.EqualsResponse(equals(anotherEntity))
    case Entity.SetChangedAndNotify(arg) => setChangedAndNotify(arg)
    case Entity.GetRelevance => sender ! Entity.GetRelevanceResponse(relevance)
    case Entity.GetCompleteness => sender ! Entity.GetCompletenessResponse(completeness)
    case Entity.Update => update()
    case Entity.GetSalience => sender ! Entity.GetSalienceResponse(salience)
    case Entity.GetAge => sender ! Entity.GetAgeResponse(getAge)
    case Entity.GetEntityType => sender ! Entity.GetEntityTypeResponse(entityType)
  }
  
  // --------------------------------------------------------------------------
  // Observable members
  // --------------------------------------------------------------------------

  /**
   * Adds an observer to the set of observers for this object, provided
   * that it is not the same as some observer already in the set.
   * The order in which notifications will be delivered to multiple
   * observers is not specified. See the class comment.
   *
   * @param   o   an observer to be added.
   * @throws NullPointerException   if the parameter o is null.
   */
  protected def addObserver(o: ActorRef) {
    if (o == null)
      throw new NullPointerException()
    if (!observers.contains(o)) {
      observers ::= o
    }
  }

  /**
   * Deletes an observer from the set of observers of this object.
   * Passing <CODE>null</CODE> to this method will have no effect.
   * @param   o   the observer to be deleted.
   */
  protected def deleteObserver(o: ActorRef) {
    observers = observers.filterNot { x => x == o }
  }

  /**
   * If this object has changed, as indicated by the
   * <code>hasChanged</code> method, then notify all of its observers
   * and then call the <code>clearChanged</code> method to indicate
   * that this object has no longer changed.
   * <p>
   * Each observer has its <code>update</code> method called with two
   * arguments: this observable object and the <code>arg</code> argument.
   *
   * @param   arg   any object.
   * @see     java.util.Observable#clearChanged()
   * @see     java.util.Observable#hasChanged()
   * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  protected def notifyObservers(arg: Any) {
    val o:Observer = null
    if (changed) {
      clearChanged()
      for { observer <- observers } {
        observer ! ObserverActor.Update(self, arg)
      }
    }
  }

  /**
   * Clears the observer list so that this object no longer has any observers.
   */
  protected def deleteObservers() {
    observers = Nil
  }

  /**
   * Marks this <tt>Observable</tt> object as having been changed; the
   * <tt>hasChanged</tt> method will now return <tt>true</tt>.
   */
  protected def setChanged() {
    changed = true
  }

  /**
   * Indicates that this object has no longer changed, or that it has
   * already notified all of its observers of its most recent change,
   * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
   * This method is called automatically by the
   * <code>notifyObservers</code> methods.
   *
   * @see     java.util.Observable#notifyObservers()
   * @see     java.util.Observable#notifyObservers(java.lang.Object)
   */
  protected def clearChanged() {
    changed = false
  }

  /**
   * Tests if this object has changed.
   *
   * @return  <code>true</code> if and only if the <code>setChanged</code>
   *          method has been called more recently than the
   *          <code>clearChanged</code> method on this object;
   *          <code>false</code> otherwise.
   * @see     java.util.Observable#clearChanged()
   * @see     java.util.Observable#setChanged()
   */
  protected def hasChanged(): Boolean = changed

  /**
   * Returns the number of observers of this <tt>Observable</tt> object.
   *
   * @return  the number of observers of this object.
   */
  protected def countObservers(): Int = observers.size

  // --------------------------------------------------------------------------
  // Local Protected Members
  // --------------------------------------------------------------------------

  protected def computeRelevance()
  protected def computeCompleteness()

  // This method contains the standard calculation for salience.
  // Children could overloaded if needed.
  protected def computeSalience() {
    salience = math.round((relevance + (100 - completeness)) / 2.0f)
  }

  //Clients will override this if there is additional update behavior
  protected def additionalUpdate() {}

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------
  
  protected def addDescriptor(descriptor: Descriptor) {
    descriptors.add(descriptor)
  }

  protected def removeDescriptor(descriptor: Descriptor): Boolean = {
    return descriptors.remove(descriptor)
  }

  protected def getDescriptors(): Set[Descriptor] = {
    return descriptors.toSet
  }

  protected def hasDescriptor(des: Descriptor): Boolean = {
    return descriptors.contains(des)
  }

  protected def getRandomDescriptor(): Descriptor = {
    var toPick = new Random().nextInt() % descriptors.size
    val i = descriptors.toIterator
    while (toPick > 0) {
      i.next()
      toPick -= 1
    }
    return i.next()
  }

  protected def equals(anotherEntity: Entity): Boolean = {
    return super.equals(anotherEntity)
  }

  protected def setChangedAndNotify(arg: Any) {
    setChanged()
    notifyObservers(arg)
  }
  
  protected def update() {
    computeRelevance()
    computeCompleteness()
    computeSalience()
    additionalUpdate()
  }

  /**
   *  This method provides the workspace update routine to determine a
   *  workspace entity's age to determine if it is old enough to be removed
   *  from the workspace
   * @return
   */
  protected def getAge(): Long = DateTime.now().getMillis - age.getMillis
}
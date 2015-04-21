package org.finfrock.starcat.workspace

import org.finfrock.starcat.core.Component
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer
import org.finfrock.starcat.structures.Entity
import org.finfrock.starcat.structures.Item
import scala.util.Random
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import org.finfrock.starcat.codelets.CodeletActor
import org.finfrock.starcat.codelets.Codelet

trait WorkspaceActor extends Component {
  // -------------------------------------------------------------------------
  // Protected Data
  // -------------------------------------------------------------------------

  private var workspaceStorage = Map[Class[_], List[Entity]]()
  private var allEntities = List[Entity]()
  private var coherence = 100.0
  private val rand = new Random()

  // --------------------------------------------------------------------------
  // Component Members
  // --------------------------------------------------------------------------

  def preExecuteCodelet(codelet: Codelet) {
    codelet.preExecuteWorkspace(self)
  }

  def executeCodelet(codelet: Codelet) {
    preExecuteCodelet(codelet)
    codelet.executeWorkspace(self)
    postExecuteCodelet(codelet)
  }

  def postExecuteCodelet(codelet: Codelet) {
    if(codelet.codeletType == Codelet.BEHAVIOR_CODELET_TYPE){
      fireCodeletEvent(codelet)
    }
    codelet.postExecuteWorkspace(self)
  }

  def update() {
    val sumOfAllRelevances:Long = (for { e <- allEntities } yield {
      e.update()
      e.getRelevance()
    }).sum

    coherence = (for { e <- allEntities } yield {
      math.round((e.getRelevance().toDouble / sumOfAllRelevances.toDouble)
        * (100.0 - e.getCompleteness().toDouble))
    }).sum
  }

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  /*
    * equivalent to calling getEntitiesMatching(Class, false, null)
    * 
    */
  def getEntitiesMatching(c: Class[Entity]): List[Entity] = getEntitiesMatching(c, false)

  /*
    * With this method, one can retrieve all Entity objects in this Workspace or
    * limit by class type
    * 
    * Class c object of Entity type to restrict matches to. If c is null, it
    * will try matches on all entities regardless of type.
    * 
    * andSubclasses--if this is set to true then all Entities of type c or a
    * subtype shall be considered
    * 
    */
  def getEntitiesMatching(c: Class[Entity], andSubclasses: Boolean): List[Entity] = {
    val entList = if (andSubclasses) {
      getListForClassAndSubclasses(c)
    } else {
      getListForClass(c)
    }

    if (entList == null) {
      return List[Entity]()
    } else {
      return entList
    }
  }

  def addEntity(entity: Entity) {
    allEntities ::= entity
    val entList = getListForClass(entity.getClass)

    workspaceStorage += entity.getClass() -> (entity :: entList)
  }

  def removeEntity(entity: Entity): Boolean = {
    allEntities = allEntities.filterNot { x => x == entity }

    val entList = getListForClass(entity.getClass())
    entity.setChangedAndNotify()
    entity.deleteObservers()

    workspaceStorage += entity.getClass() -> entList.filterNot { e => e == entity }

    return true
  }

  def containsEntity(entity: Entity): Boolean = {
    val entList = getListForClass(entity.getClass())
    return entList.contains(entity)
  }

  def getObjectBySalience(c: Class[Entity]): Item = {
    val list = getEntitiesMatching(c)
    return getObjectBySalienceFromList(list)
  }

  def getTotallyRandomObject(): Item = {
    return getObjectBySalienceFromList(allEntities);
  }

  def getObjectBySalience(c: Class[Entity], andSubclasses: Boolean): Item = {
    val list = getEntitiesMatching(c, andSubclasses)
    return getObjectBySalienceFromList(list)
  }

  // --------------------------------------------------------------------------
  // Private Members
  // --------------------------------------------------------------------------

  /*
    * c--Class of Entity to search for. If c == null, then all Entity objects
    * will be returned.
    * 
    * returns modifiable List of Entity objects
    */
  private def getListForClass(className: Class[_]): List[Entity] = {
    workspaceStorage.get(className) match {
      case Some(entities) => entities
      case None           => Nil
    }
  }

  private def getListForClassAndSubclasses(c: Class[Entity]): List[Entity] = {
    (for {
      cls <- workspaceStorage.keys
      if (c.isAssignableFrom(cls))
    } yield {
      workspaceStorage(cls)
    }).flatten.toList
  }

  private def getObjectBySalienceFromList(list: List[Entity]): Item = {
    val sum = list.map(_.getSalience()).sum

    val stopVal = rand.nextInt(sum + 1)

    val it = new WorkspaceIterator(list)

    var salienceEntity: Entity = null
    var salienceSum = 0

    while (it.hasNext() && salienceSum < stopVal) {
      salienceEntity = it.next()
      salienceSum += salienceEntity.getSalience()
    }

    return salienceEntity.asInstanceOf[Item]
  }
}
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
import org.finfrock.starcat.util.RandomIterator
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.Future

object WorkspaceActor {
  case class UpdateCoherence(coherence:Double)
  object GetCoherence
  case class GetCoherenceResponse(coherence:Double)
}

trait WorkspaceActor extends Component {
  // -------------------------------------------------------------------------
  // Protected Data
  // -------------------------------------------------------------------------

  private var workspaceStorage = Map[String, List[ActorRef]]()
  private var allEntities = List[ActorRef]()
  private var coherence = 100.0
  private val rand = new Random()
  implicit val timeout = Timeout(5 seconds)
  implicit val ex = context.dispatcher

  override def receive = super.receive orElse {
    case WorkspaceActor.UpdateCoherence(updatedCoherence) => coherence = updatedCoherence
    case WorkspaceActor.GetCoherence => sender ! WorkspaceActor.GetCoherenceResponse(coherence)
  }
  
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
    case class EntityData(relevance:Int, completeness:Int)
    allEntities.foreach(_ ! Entity.Update)

    val listOfFutureEntityData = for { entity <- allEntities } yield {
      for{relevanceResponse <- ask(entity, Entity.GetRelevance).mapTo[Entity.GetRelevanceResponse]
      completenessResponse <- ask(entity, Entity.GetCompleteness).mapTo[Entity.GetCompletenessResponse]} yield{
        EntityData(relevanceResponse.relevance, completenessResponse.completeness)
      }
    }
    
    val futrueListOfEntityData = Future.sequence(listOfFutureEntityData)
    
    val futureSumOfAllRelevances = futrueListOfEntityData.map(_.map(_.relevance.toDouble).sum)

    val futureUpdateCoherence = for {
      entityDatas <- futrueListOfEntityData
      sumOfAllRelevances <- futureSumOfAllRelevances
    } yield {
      val updatingCoherence = (for { entityData <- entityDatas } yield {
        math.round((entityData.relevance.toDouble / sumOfAllRelevances)
          * (100.0 - entityData.completeness.toDouble))
      }).sum
      
      WorkspaceActor.UpdateCoherence(updatingCoherence)
    }
    
    futureUpdateCoherence.pipeTo(self)
  }

  // --------------------------------------------------------------------------
  // Public Members
  // --------------------------------------------------------------------------

  /*
    * equivalent to calling getEntitiesMatching(Class, false, null)
    * 
    */
  def getEntitiesMatching(entityType:String): List[ActorRef] = {
    val entList = getListForType(entityType)

    if (entList == null) {
      return List[ActorRef]()
    } else {
      return entList
    }
  }

  def addEntity(entityType:String, entity: ActorRef) {
    allEntities ::= entity
    val entList = getListForType(entityType)

    workspaceStorage += entityType -> (entity :: entList)
  }

  def removeEntity(entityType:String, entity: ActorRef): Boolean = {
    allEntities = allEntities.filterNot { x => x == entity }

    val entList = getListForType(entityType)
    entity ! Entity.SetChangedAndNotify(null)
    entity ! Entity.DeleteObservers

    workspaceStorage += entityType -> entList.filterNot { e => e == entity }

    return true
  }

  def containsEntity(entityType:String, entity: Entity): Boolean = {
    val entList = getListForType(entityType)
    return entList.contains(entity)
  }

  def getObjectBySalience(entityType:String): Future[ActorRef] = {
    val list = getEntitiesMatching(entityType)
    return getObjectBySalienceFromList(list)
  }

  def getTotallyRandomObject(): Future[ActorRef] = {
    return getObjectBySalienceFromList(allEntities)
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
  private def getListForType(entityType: String): List[ActorRef] = {
    workspaceStorage.get(entityType) match {
      case Some(entities) => entities
      case None           => Nil
    }
  }

  private def getObjectBySalienceFromList(list: List[ActorRef]): Future[ActorRef] = {
    case class EntitySalience(entity:ActorRef, salience:Int)
    val listOfFutureEntitySalience = for { entity <- allEntities } yield {
      for{salienceResponse <- ask(entity, Entity.GetSalience).mapTo[Entity.GetSalienceResponse]} yield{
        EntitySalience(entity, salienceResponse.salience)
      }
    }
     
    val futrueListOfEntitySalience = Future.sequence(listOfFutureEntitySalience)
    
    val futureSumOfAllSalience = futrueListOfEntitySalience.map(_.map(_.salience).sum)

    val futureEntity = for {
      listOfEntitySalience <- futrueListOfEntitySalience
      sumOfAllSalience <- futureSumOfAllSalience
    } yield {
      val stopVal = rand.nextInt(sumOfAllSalience + 1)

      val it = new RandomIterator(listOfEntitySalience)

      var salienceEntity: EntitySalience = null
      var salienceSum = 0

      while (it.hasNext() && salienceSum < stopVal) {
        salienceEntity = it.next()
        salienceSum += salienceEntity.salience
      }

      salienceEntity.entity
    }
    
    futureEntity
  }
}
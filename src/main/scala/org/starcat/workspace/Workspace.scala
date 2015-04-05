package org.starcat.workspace

import org.starcat.core.Component
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer
import org.starcat.codelets.Codelet
import org.starcat.structures.Entity
import org.starcat.structures.Item
import scala.util.Random

class Workspace extends Component {
  // -------------------------------------------------------------------------
    // Protected Data
  // -------------------------------------------------------------------------

   protected var workspaceStorage = Map[Class[_], List[Entity]]()

   protected var allEntities = List[Entity]()

   protected var coherence = 100.0;

   // --------------------------------------------------------------------------
   // Component Members
   // --------------------------------------------------------------------------

   protected def preExecuteCodelet(codelet:Codelet)
   {
      codelet.preExecute(this);
   }

   def executeCodelet(codelet:Codelet)
   {
      preExecuteCodelet(codelet)
      codelet.execute(this)
      postExecuteCodelet(codelet)
   }

   protected def postExecuteCodelet(codelet:Codelet) {
      codelet.postExecute(this);
   }
   
   def update() {
      var sumOfAllRelevances = 0l;

      for{e <- allEntities} {
         e.update()
         sumOfAllRelevances += e.getRelevance()
      }

      var temp_coherence = 0.0;
      
      for{ e <- allEntities} {
         temp_coherence += math.round((e.getRelevance().toDouble / sumOfAllRelevances.toDouble)
                  * (100.0 - e.getCompleteness().toDouble))
      }
      coherence = temp_coherence;
   }

   // --------------------------------------------------------------------------
   // Public Members
   // --------------------------------------------------------------------------

   /*
    * equivalent to calling getEntitiesMatching(Class, false, null)
    * 
    */
   def getEntitiesMatching(c:Class[Entity]):List[Entity] = getEntitiesMatching(c, false)

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
   def getEntitiesMatching(c:Class[Entity], andSubclasses:Boolean):List[Entity] ={
      val entList = if (andSubclasses) {
         getListForClassAndSubclasses(c)
      }
      else{
         getListForClass(c)
      }
      
      if (entList == null){
         return List[Entity]()
      }
      else {
         return entList
      }
   }
   

   def addEntity(entity:Entity) {
      allEntities ::= entity
      val entList = getListForClass(entity.getClass)
      
      workspaceStorage += entity.getClass() -> (entity :: entList)
   }

   def removeEntity(entity:Entity):Boolean = {
     allEntities = allEntities.filterNot { x => x == entity }

      val entList = getListForClass(entity.getClass())
      entity.setChangedAndNotify()
      entity.deleteObservers()
      
      workspaceStorage += entity.getClass() -> entList.filterNot { e => e == entity }
      
      return true;
   }

  def containsEntity(entity: Entity): Boolean = {
    val entList = getListForClass(entity.getClass())
    return entList.contains(entity)
  }

  protected def unregisterCodeletPrivate(notUsedCodelet: Codelet) {
    getCurrentCodelet() match {
      case Some(currentCodelete) => {
        fireCodeletEvent(currentCodelete)
        unregisterCodelet(currentCodelete)
      }
      case None => // do nothing
    }
  }

   def getObjectBySalience(c:Class[Entity]):Item = {
      val list = getEntitiesMatching(c)
      return getObjectBySalienceFromList(list)
   }

   def getTotallyRandomObject():Item = {
      return getObjectBySalienceFromList(allEntities);
   }

   def getObjectBySalience(c:Class[Entity], andSubclasses:Boolean):Item = {
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
   private def getListForClass(className:Class[_]):List[Entity] = {
      workspaceStorage.get(className) match{
        case Some(entities) => entities
        case None => Nil
      }
   }

   private def getListForClassAndSubclasses(c:Class[Entity]):List[Entity] = {
      (for{ cls <- workspaceStorage.keys
        if(c.isAssignableFrom(cls))} yield{
        workspaceStorage(cls)
      }).flatten.toList
   }
   
   private def getObjectBySalienceFromList(list:List[Entity]):Item = {
      var wo:Item = null;
      var sum = 0;

      for{entity <- list} {
         sum += entity.getSalience();
      }

      // Flat distribution
      val rand = new Random();
      val stopVal = rand.nextInt(sum + 1)
      
      /*
       * set to use workspace iterator so that items are selected randomly form
       * list Normal iterator does not allow this.
       */
      val it = new WorkspaceIterator(list);

      var salienceEntity:Entity = null;
      val salienceSum = 0;

      while (it.hasNext() && sum < stopVal) {
         salienceEntity = it.next()
         sum += salienceEntity.getSalience();
      }

      return salienceEntity.asInstanceOf[Item]
   }
}
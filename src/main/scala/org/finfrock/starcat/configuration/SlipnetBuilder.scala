package org.finfrock.starcat.configuration

import java.util.UUID
import org.finfrock.starcat.slipnet.Link
import akka.actor.ActorRef
import org.finfrock.starcat.slipnet.SlipnetActor
import org.finfrock.starcat.slipnet.SlipnetNodeActor
import org.finfrock.starcat.codelets.Codelet
import akka.actor.Props
import org.joda.time.DateTime
import akka.actor.ActorSystem

class SlipnetBuilder(system:ActorSystem) {
  // -------------------------------------------------------------------------
  //  Public Members
  // -------------------------------------------------------------------------
  
  private val slipnet = system.actorOf(SlipnetActor.getProps(), "Slipnet")

  protected def getSlipnet() = slipnet 
  
  private var slipnetCollection = Map[String, ActorRef]()
  
  def getSlipnetNode(slipnetNodeName:String):ActorRef = 
    slipnetCollection(slipnetNodeName)
    
  // -------------------------------------------------------------------------
  // Protected Members
  // -------------------------------------------------------------------------

  protected def createCodelet(codelet:Codelet, slipnetNodeName:String){
    val slipnetNode = getSlipnetNode(slipnetNodeName)
    
    slipnetNode ! SlipnetNodeActor.AddCodelet(codelet)
  }
  
  protected def createCodelet(props:Props, urgency:Double = 0, numberToEmit:Int = 0, 
      codeletType:String = Codelet.BEHAVIOR_CODELET_TYPE, 
      name:String = UUID.randomUUID().toString(), 
      timeToDie:DateTime = DateTime.now().plusYears(1), slipnetNodeName:String){
    
    val actorRef = system.actorOf(props, name)
    val newCodelet = new Codelet(name = name, codeletActor = actorRef, numberToEmit = numberToEmit, 
        codeletType = Codelet.BEHAVIOR_CODELET_TYPE, timeToDie = timeToDie)
    
    createCodelet(newCodelet, slipnetNodeName)
  }
  
  /**
   * Create a one way link
   * 
   * @param linkBegingBuilt
   *            The link being created
   * @param fromSlipnetNode
   *            the source of the spread of activation
   * @param toSlipnetNode
   *            the receiver of the spread of activation
   * @param intrinsicLength
   *            The percentage of activation from the source (from) to
   *            receiver (to)
   * @param labelNode
   */
  protected def createLink(fromSlipnetNodeName:String,
      toSlipnetNodeName:String, intrinsicLength:Int) {
    
    val name = "Link - " + fromSlipnetNodeName + " -> " + toSlipnetNodeName;
    val toSlipnetNode = getSlipnetNode(toSlipnetNodeName)
    val fromSlipnetNode = getSlipnetNode(fromSlipnetNodeName)
    val linkBegingBuilt = new Link(name, intrinsicLength, fromSlipnetNode, toSlipnetNode)
    
    fromSlipnetNode ! SlipnetNodeActor.AddLateralLink(linkBegingBuilt)
  }

  /**
   * Create a slipnet node
   * 
   * @param name
   *            the name of the slipnet node
   * @param memoryLevel
   *            The higher the value the longer
   *            memory is stored in the node.
   * @param initalActivation
   *            the initial activation value
   * @param activationThreashold
   *            This is the level that the slipnet has to be before it places 
   *            an assigned codelet into the coderack
   */
  protected def createSlipnetNode(name:String, memoryLevel:Int,
      initalActivation:Int, activationThreashold:Int) {
    
    val newNode = system.actorOf(
      SlipnetNodeActor.getProps(name, memoryLevel, initalActivation, activationThreashold, slipnet), name)

    slipnetCollection += name -> newNode
    slipnet ! SlipnetActor.AddSlipnetNode(newNode)
  }
}